package examples;

import java.util.Collection;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.SecurityGroup;
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
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyResult;
import com.amazonaws.services.identitymanagement.model.CreateRoleRequest;
import com.amazonaws.services.identitymanagement.model.CreateRoleResult;
import com.amazonaws.services.iot.model.Status;

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
  
  private AmazonECS ecs = AmazonECSAsyncClientBuilder.standard().build();
  private AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().build();
  private AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.standard().build();
  private AmazonElasticLoadBalancing elb = AmazonElasticLoadBalancingClientBuilder.standard().build();

  
  public Setup() {
    super();
  }

  /* Create 2 security groups. One for the instances and one for the load balancer */
  // Security group instances: open inbound ports 80, 8080, 32768 - 65535 and 22
  // Security group load balancer: open inbound ports 80, 8080 and 22
  
  private CreateSecurityGroupResult createSecurityGroup(String groupName, String description) {
    CreateSecurityGroupRequest request = new CreateSecurityGroupRequest()
        .withGroupName(groupName)
        .withDescription(description);
    CreateSecurityGroupResult result = this.ec2.createSecurityGroup(request);
    System.out.println(result.toString());
    return result;
  }
  
  public void createInstancesSecurityGroup() {
    this.instancesSecurityGroupId = 
        createSecurityGroup(instancesSecurityGroupName, "Security Group for Bungee VM instances")
        .getGroupId();
    AuthorizeSecurityGroupIngressRequest request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(32768).withToPort(65535).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.instancesSecurityGroupName);
    
    ec2.authorizeSecurityGroupIngress(request);
    
    request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(8080).withToPort(8080).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.instancesSecurityGroupName);
    ec2.authorizeSecurityGroupIngress(request);
    
    request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(22).withToPort(22).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.instancesSecurityGroupName);
    ec2.authorizeSecurityGroupIngress(request);
  }
  
  public void createLoadBalancerSecurityGroup() {
    this.loadBalancerSecurityGroupId = 
        createSecurityGroup(loadBalancerSecurityGroupName, "Security Group for Bungee load balancer").getGroupId();
    
    AuthorizeSecurityGroupIngressRequest request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(8080).withToPort(8080).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.loadBalancerSecurityGroupName);
    
    ec2.authorizeSecurityGroupIngress(request);
    
    
    request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(22).withToPort(22).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.loadBalancerSecurityGroupName);
    
    ec2.authorizeSecurityGroupIngress(request);
    
    request = new AuthorizeSecurityGroupIngressRequest()
        .withFromPort(80).withToPort(80).withCidrIp("0.0.0.0/0")
        .withIpProtocol("TCP")
        .withGroupName(this.loadBalancerSecurityGroupName);
    
    ec2.authorizeSecurityGroupIngress(request);
  }
  
 
  
  /* Create key pair */ 
  // TODO: see if this is needed. Probably not
  

  
  /* Create a cluster called bungeeCluster */
  
  public CreateClusterResult createCluster() {
      CreateClusterRequest request = new CreateClusterRequest().withClusterName(clusterName);
      System.out.println("Creating Cluster");
      CreateClusterResult result = this.ecs.createCluster(request);
      return result;
  }
  
  /*// TODO Create a role called ecsServiceRole
   This doesn't work. Remove or fix

  
  public AttachRolePolicyResult CreateIAMRoles(){
    CreateRoleRequest request = new CreateRoleRequest()
        .withRoleName("BungeeEcsServicerole")
        .withDescription("Role for the ECS Service")
        .withAssumeRolePolicyDocument("");
    
    CreateRoleResult result = iam.createRole(request);
    System.out.println(result.toString());
    
    AttachRolePolicyRequest policyReq = new AttachRolePolicyRequest()
        .withRoleName("BungeeEcsServicerole")
        .withPolicyArn("arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceRole");
    AttachRolePolicyResult policyRslt = iam.attachRolePolicy(policyReq);
    return policyRslt;
    
  }*/
  
  
  public void createApplicationLoadBalancer(String subnet1, String subnet2) {
    createLoadBalancer(subnet1, subnet2);
    createTargetGroup();
    createListener();
  }
  
  // TODO Create a LoadBalancer
  private CreateLoadBalancerResult createLoadBalancer(String subnet1, String subnet2) {
    CreateLoadBalancerRequest request = new CreateLoadBalancerRequest()
        .withName(this.loadBalancerName)
        .withSecurityGroups(this.loadBalancerSecurityGroupId)
        .withSubnets(subnet1, subnet2);
    
    CreateLoadBalancerResult result  = elb.createLoadBalancer(request);
    this.loadBalancerArn = result.getLoadBalancers().get(0).getLoadBalancerArn();
    return result;
  
  }
 
  
  // TODO Create a LoadBalancer target group
  
  private CreateTargetGroupResult createTargetGroup() {
    
    DescribeVpcsRequest vpcReq = new DescribeVpcsRequest().withFilters(new Filter().withName("isDefault").withValues("true"));
    String defaultVpcId = ec2.describeVpcs(vpcReq).getVpcs().get(0).getVpcId().toString();
    
    CreateTargetGroupRequest request = new CreateTargetGroupRequest()
        .withHealthCheckIntervalSeconds(5)
        .withHealthCheckPath("/?size=1")
        .withHealthCheckTimeoutSeconds(2)
        .withPort(8080)
        .withProtocol("HTTP")
        .withName("TargetGroupBungee")
        .withVpcId(defaultVpcId);
    
    CreateTargetGroupResult result = this.elb.createTargetGroup(request);
    this.targetGroupArn = result.getTargetGroups().get(0).getTargetGroupArn();
    return result;
    
  }
  
  // create listener
  public CreateListenerResult createListener() {
    CreateListenerRequest request = new CreateListenerRequest()
        .withLoadBalancerArn(this.loadBalancerArn)
        .withPort(8080).withProtocol("HTTP")
        .withDefaultActions(new Action()
            .withType(ActionTypeEnum.Forward)
            .withTargetGroupArn(this.targetGroupArn));
    CreateListenerResult result = elb.createListener(request);
    return result;
  }
  
  /* Create a service called bungeeService. Use public docker container and test */
  
  public CreateServiceResult createEcsService(String ecsServiceRoleArn, int desiredCount, int maximumPercent,
      int minimumHealthyPercent) {

    if (this.targetGroupArn == null) {
      System.out.println("No target group arn defined. Run createApplicationLoadBalancer before createEcsService");
      System.exit(0);
    }    
    ContainerDefinition container = new ContainerDefinition()
        .withImage("bungee")
        .withName(this.containerName)
        .withCpu(400)
        .withPortMappings(new PortMapping().withHostPort(0).withContainerPort(8080));
    RegisterTaskDefinitionRequest taskreq =
        new RegisterTaskDefinitionRequest().withContainerDefinitions(container)
            .withFamily("BungeeTaskDefinitionFamily")
            .withCpu("400")
            .withMemory("400");

    RegisterTaskDefinitionResult rslt = ecs.registerTaskDefinition(taskreq);

    DeploymentConfiguration conf = new DeploymentConfiguration().withMaximumPercent(maximumPercent)
        .withMinimumHealthyPercent(minimumHealthyPercent);
    
    CreateServiceRequest request = new CreateServiceRequest().withDesiredCount(desiredCount)
        .withCluster(this.clusterName)
        .withServiceName(this.serviceName)
        .withTaskDefinition("BungeeTaskDefinitionFamily")
        .withRole("ecsServiceRole")
        .withDeploymentConfiguration(conf)
        .withLoadBalancers(new LoadBalancer()
            .withContainerName(this.containerName)
            .withContainerPort(8080)
            .withTargetGroupArn(this.targetGroupArn)
            //.withLoadBalancerName(this.loadBalancerName)
            );
    CreateServiceResult result = this.ecs.createService(request);
    return result;
  }

  
  //TODO
  public void createTasks(int numTasks) {
    
  }
  
  // TODO
  public void createInstances(int numInstances) {
    
  }
  
  // TODO
  public void terminateAllInstances() {
    
  }
  /* Write function that creates X tasks */
  
  /* Write function that creates X instances */ 
  
  /* Write tear down function that terminates the instances*/
  
}
