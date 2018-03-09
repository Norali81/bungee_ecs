package examples;

import java.util.Arrays;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSAsyncClientBuilder;
import com.amazonaws.services.ecs.model.ContainerDefinition;
import com.amazonaws.services.ecs.model.CreateServiceRequest;
import com.amazonaws.services.ecs.model.CreateServiceResult;
import com.amazonaws.services.ecs.model.DeploymentConfiguration;
import com.amazonaws.services.ecs.model.LoadBalancer;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionRequest;
import com.amazonaws.services.ecs.model.RegisterTaskDefinitionResult;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancingv2.model.Action;
import com.amazonaws.services.elasticloadbalancingv2.model.ActionTypeEnum;
import com.amazonaws.services.elasticloadbalancingv2.model.CreateListenerRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.CreateListenerResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupsRequest;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.GetPolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetPolicyResult;
import tools.descartes.bungee.cloud.aws.ecs.services.AwsEcsService;

public class test {

  public static void main(String[] args) {
    
    //Set ttl for dns in JVM to avoid "Unknown host error"
    // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-jvm-ttl.html
    java.security.Security.setProperty("networkaddress.cache.ttl" , "10");

    
   //AwsEcsService EcsService = new AwsEcsService("HelloWorld", "TestService");
   //EcsService.getRunningCount();
    
    Setup setup = new Setup();
    // create security group
    //setup.createLoadBalancerSecurityGroup();
    // create cluster
    //setup.createCluster();
   
    
    // create load balancer security group
    //setup.createLoadBalancerSecurityGroup();
  
    // create load balancer
    //System.out.println(setup.createLoadBalancer("subnet-560c340d", "subnet-6270b22a"));
   // setup.createApplicationLoadBalancer("subnet-560c340d", "subnet-6270b22a");
    AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().build();
    AmazonElasticLoadBalancing elb = AmazonElasticLoadBalancingClientBuilder.standard().build();
    AmazonECS ecs = AmazonECSAsyncClientBuilder.standard().build();
   // DescribeVpcsRequest request = new DescribeVpcsRequest().withFilters(new Filter().withName("isDefault").withValues("true"));
   // System.out.println(ec2.describeVpcs(request).getVpcs().get(0).getVpcId().toString());
    
   //CreateServiceResult result =  setup.createEcsService("arn:aws:iam::095867673188:role/ecsServiceRole", 5, 200,50);
   // System.out.println(result.toString());
    
  System.out.println(elb.describeTargetGroups(new DescribeTargetGroupsRequest()).toString());
    
  }
}
