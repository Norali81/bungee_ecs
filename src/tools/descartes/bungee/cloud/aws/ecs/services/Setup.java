package tools.descartes.bungee.cloud.aws.ecs.services;

import java.util.Collection;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.IamInstanceProfileSpecification;
import com.amazonaws.services.ec2.model.ResourceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagSpecification;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSAsyncClientBuilder;
import com.amazonaws.services.ecs.model.ContainerDefinition;
import com.amazonaws.services.ecs.model.CreateClusterRequest;
import com.amazonaws.services.ecs.model.CreateClusterResult;
import com.amazonaws.services.ecs.model.CreateServiceRequest;
import com.amazonaws.services.ecs.model.CreateServiceResult;
import com.amazonaws.services.ecs.model.DeploymentConfiguration;
import com.amazonaws.services.ecs.model.LoadBalancer;
import com.amazonaws.services.ecs.model.PortMapping;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionRequest;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionResult;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancingv2.model.Action;
import com.amazonaws.services.elasticloadbalancingv2.model.ActionTypeEnum;
import com.amazonaws.services.elasticloadbalancingv2.model.CreateListenerRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.CreateListenerResult;
import com.amazonaws.services.elasticloadbalancingv2.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancingv2.model.CreateTargetGroupRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.CreateTargetGroupResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupsRequest;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyResult;
import com.amazonaws.services.identitymanagement.model.CreateRoleRequest;
import com.amazonaws.services.identitymanagement.model.CreateRoleResult;
import com.amazonaws.services.iot.model.Status;

/**
 * This class contains all functionality to create multiple items
 * in the AWS interface. 
 * @author nora
 *
 */

public class Setup {

  private String instancesSecurityGroupName = "BungeeInstances";
  private String loadBalancerSecurityGroupName = "BungeeLoadBalancer";
  private String containerName = "OfficialBungeeContainer";
  private String loadBalancerSecurityGroupId;
  private String instancesSecurityGroupId;
  private String loadBalancerArn;
  private String loadBalancerName="BungeeAppLoadBalancer";
  private String targetGroupArn;
  private String clusterName = "BungeeCluster";
  private String serviceName = "BungeeService";
  private String targetGroupName = "TargetGroupBungee";
  
  private AmazonECS ecs = AmazonECSAsyncClientBuilder.standard().build();
  private AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().build();
  private AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.standard().build();
  private AmazonElasticLoadBalancing elb = AmazonElasticLoadBalancingClientBuilder.standard().build();

  /**
   * Constructor
   */
  public Setup() {
    super();
  }

  /**
   * Creates a security group 
   * @param groupName
   * @param description
   * @return The result object 
   */
  private CreateSecurityGroupResult createSecurityGroup(String groupName, String description) {
    CreateSecurityGroupRequest request = new CreateSecurityGroupRequest()
        .withGroupName(groupName)
        .withDescription(description);
    CreateSecurityGroupResult result = this.ec2.createSecurityGroup(request);
    System.out.println(result.toString());
    return result;
  }
  
  /**
   * Create security group for EC2 instances
   */
  public void createInstancesSecurityGroup() {
    System.out.println("Creating security group for instances");
    
    this.instancesSecurityGroupId = 
        createSecurityGroup(instancesSecurityGroupName, "Security Group for Bungee VM instances")
        .getGroupId();
    
    // Authorize port range needed for ECS
    AuthorizeSecurityGroupIngressRequest request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(32768).withToPort(65535).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.instancesSecurityGroupName);
    
    ec2.authorizeSecurityGroupIngress(request);
    
    // Authorize port 8080
    request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(8080).withToPort(8080).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.instancesSecurityGroupName);
    ec2.authorizeSecurityGroupIngress(request);
    
    // authorize port 22
    request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(22).withToPort(22).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.instancesSecurityGroupName);
    ec2.authorizeSecurityGroupIngress(request);
  }
  
  /**
   * Create security group for load balancer
   */
  public void createLoadBalancerSecurityGroup() {
    
    System.out.println("Creating security group for load balancer");
    this.loadBalancerSecurityGroupId = 
        createSecurityGroup(loadBalancerSecurityGroupName, "Security Group for Bungee load balancer").getGroupId();
    
    // authorize port 8080
    AuthorizeSecurityGroupIngressRequest request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(8080).withToPort(8080).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.loadBalancerSecurityGroupName);
    
    System.out.println("Authorizing ingress ports");
    ec2.authorizeSecurityGroupIngress(request);
    
    // authorize port 22
    request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(22).withToPort(22).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.loadBalancerSecurityGroupName);
    
    ec2.authorizeSecurityGroupIngress(request);
    
    //authorize port 80
    request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(80).withToPort(80).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.loadBalancerSecurityGroupName);
    
    ec2.authorizeSecurityGroupIngress(request);
  }

  
  /***
   * Create a cluster called bungeeCluster
   * @return Result object
   */
  public CreateClusterResult createCluster() {
      CreateClusterRequest request = new CreateClusterRequest().withClusterName(clusterName);
      System.out.println("Creating Cluster");
      CreateClusterResult result = this.ecs.createCluster(request);
      return result;
  }
  
  
  
  
  /**
   * Creates an application load balancer
   * @param subnet1
   * @param subnet2
   * @param subnet3
   * @return Result object
   */
  private CreateLoadBalancerResult createLoadBalancer(String subnet1, String subnet2, String subnet3) {
    CreateLoadBalancerRequest request = new CreateLoadBalancerRequest()
        .withName(this.loadBalancerName)
        .withSecurityGroups(ec2.describeSecurityGroups(new DescribeSecurityGroupsRequest()
            .withGroupNames(this.loadBalancerSecurityGroupName)).getSecurityGroups().get(0).getGroupId().toString())
        .withSubnets(subnet1, subnet2, subnet3);
    
    System.out.println("Creating load balancer");
    CreateLoadBalancerResult result  = elb.createLoadBalancer(request);
    this.loadBalancerArn = result.getLoadBalancers().get(0).getLoadBalancerArn();
    return result;
  
  }
  
  /**
   * Calls function to create load balancer
   * with specific parameters
   * @param subnet1
   * @param subnet2
   * @param subnet3
   */
  public void createApplicationLoadBalancer(String subnet1, String subnet2, String subnet3) {
    createLoadBalancer(subnet1, subnet2, subnet3);
    createTargetGroup();
    createListener();
  }
 
  
  /**
   * Create a LoadBalancer target group
   * @return Result object
   */
  private CreateTargetGroupResult createTargetGroup() {
    
    DescribeVpcsRequest vpcReq = new DescribeVpcsRequest().withFilters(new Filter().withName("isDefault").withValues("true"));
    String defaultVpcId = ec2.describeVpcs(vpcReq).getVpcs().get(0).getVpcId().toString();
    
    CreateTargetGroupRequest request = new CreateTargetGroupRequest()
        .withHealthCheckIntervalSeconds(50)
        .withHealthCheckPath("/?size=1")
        .withHealthCheckTimeoutSeconds(30)
        .withUnhealthyThresholdCount(10)
        .withHealthyThresholdCount(2)
        .withPort(8080)
        .withProtocol("HTTP")
        .withName(this.targetGroupName)
        .withVpcId(defaultVpcId);
    
    System.out.println("Creating target group");
    CreateTargetGroupResult result = this.elb.createTargetGroup(request);
    this.targetGroupArn = result.getTargetGroups().get(0).getTargetGroupArn();
    return result;
    
  }
  
  /**
   * Create a listener
   * @return Result object. 
   */
  public CreateListenerResult createListener() {
    CreateListenerRequest request = new CreateListenerRequest()
        .withLoadBalancerArn(this.loadBalancerArn)
        .withPort(8080).withProtocol("HTTP")
        .withDefaultActions(new Action()
            .withType(ActionTypeEnum.Forward)
            .withTargetGroupArn(elb.describeTargetGroups(new DescribeTargetGroupsRequest()
                .withNames(this.targetGroupName))
                .getTargetGroups().get(0)
                .getTargetGroupArn()));
    System.out.println("Creating listener");
    CreateListenerResult result = elb.createListener(request);
    return result;
  }
  
  /**
   * Create a service called bungeeService 
   * @param ecsServiceRoleArn
   * @param desiredCount
   * @param maximumPercent
   * @param minimumHealthyPercent
   * @param image
   * @return Result object
   */
  
  public CreateServiceResult createEcsService(String ecsServiceRoleArn, int desiredCount, int maximumPercent,
      int minimumHealthyPercent, String image) {
 
    // Create a container definition
    ContainerDefinition container = new ContainerDefinition()
        .withImage(image) // "095867673188.dkr.ecr.eu-west-1.amazonaws.com/bungee"
        .withName(this.containerName)
        .withCpu(400)
        .withPortMappings(new PortMapping().withHostPort(0).withContainerPort(8080));
    
    // register task definition
    RegisterTaskDefinitionRequest taskreq =
        new RegisterTaskDefinitionRequest().withContainerDefinitions(container)
            .withFamily("BungeeTaskDefinitionFamily")
            .withCpu("450")
            .withMemory("450");

    RegisterTaskDefinitionResult rslt = ecs.registerTaskDefinition(taskreq);

    DeploymentConfiguration conf = new DeploymentConfiguration().withMaximumPercent(maximumPercent)
        .withMinimumHealthyPercent(minimumHealthyPercent);
    
    // create a service
    CreateServiceRequest request = new CreateServiceRequest().withDesiredCount(desiredCount)
        .withCluster(this.clusterName)
        .withServiceName(this.serviceName)
        .withTaskDefinition("BungeeTaskDefinitionFamily")
        .withRole("ecsServiceRole")
        .withDeploymentConfiguration(conf)
        .withLoadBalancers(new LoadBalancer()
            .withContainerName(this.containerName)
            .withContainerPort(8080)
            .withTargetGroupArn(elb.describeTargetGroups(new DescribeTargetGroupsRequest()
                .withNames(this.targetGroupName))
                .getTargetGroups().get(0)
                .getTargetGroupArn())
            );
    System.out.println("Creating service");
    CreateServiceResult result = this.ecs.createService(request);
    return result;
  }

 
  /**
   * Create desired number of instances
   * @param numInstances
   * @param instanceType
   * @param keyPair
   * @param userData
   * @param imageID
   * @param iamInstanceProfile
   */
  public void createInstances(int numInstances, String instanceType, String keyPair,
      String userData, String imageID, String iamInstanceProfile) {

    for (int i = 1; i <= numInstances; i++) {

      System.out.println("Creating instance " + i);
      RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

      System.out.println(ec2
          .describeSecurityGroups(
              new DescribeSecurityGroupsRequest().withGroupNames(this.instancesSecurityGroupName))
          .getSecurityGroups()
          .get(0)
          .getGroupId()
          .toString());

      // run instance
      runInstancesRequest.withImageId(imageID) // ami-0693ed7f ECS optimized image for eu-west-1
          .withInstanceType(instanceType)
          .withMinCount(1)
          .withMaxCount(1)
          .withKeyName(keyPair)
          .withSecurityGroups(this.instancesSecurityGroupName)
          .withTagSpecifications(new TagSpecification()
              .withTags(new Tag().withValue("BungeeServiceInstance")
                  .withKey("Name"))
              .withResourceType(ResourceType.Instance))
          .withIamInstanceProfile(new IamInstanceProfileSpecification().withName(iamInstanceProfile) // ecsInstanceRole
          )
          .withUserData(userData);
      RunInstancesResult result = this.ec2.runInstances(runInstancesRequest);
      System.out.println(result.toString());
    }
  }  
}
