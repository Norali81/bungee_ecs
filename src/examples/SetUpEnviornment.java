package examples;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSAsyncClientBuilder;
import com.amazonaws.services.ecs.model.CreateServiceResult;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder;
import tools.descartes.bungee.cloud.aws.ecs.services.Setup;

public class SetUpEnviornment {

  public static void main(String[] args) {
    //Set ttl for dns in JVM to avoid "Unknown host error"
    // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-jvm-ttl.html
    java.security.Security.setProperty("networkaddress.cache.ttl" , "0");

    /* Variables. Replace here with your own values */
    String subnet1 = "subnet-560c340d";
    String subnet2 = "subnet-6270b22a";
    String subnet3 = "subnet-b33ac5d5";
    String containerRegistryUrl = "095867673188.dkr.ecr.eu-west-1.amazonaws.com/bungee";
    String ecsServiceRoleArn = "arn:aws:iam::095867673188:role/ecsServiceRole";
    String awsKeyName = "aws-nora3"; // name of your .pem file in the folder ~/.ssh
    String instanceType = "t2.micro"; // instance type m1.small
    String instanceImage = "ami-64c4871d"; //ECS optimised image for EU west 1  ami-0693ed7f 
    int numberOfDesiredTasks = 1;
    
    Setup setup = new Setup();
    
    // create security groups
    setup.createLoadBalancerSecurityGroup();
    setup.createInstancesSecurityGroup();
    
    
    // create cluster
    setup.createCluster();
  
    // create load balancer
    setup.createApplicationLoadBalancer(subnet1, subnet2, subnet3);

    // create a service 
   CreateServiceResult result =  setup.createEcsService(ecsServiceRoleArn, numberOfDesiredTasks, 200,50, containerRegistryUrl);
   System.out.println(result.toString());
  
    
    // Run instances
    setup.createInstances(1, instanceType, awsKeyName,
        // This is a base 64 encoded shell script to set up the container instances
        "IyEvYmluL3NoDQoNCg0KRklMRT0iL2V0Yy9lY3MvZWNzLmNvbmZpZyINCg0KZWNobyAiRUNTX0NMVVNURVI9QnVuZ2VlQ2x1c3RlciIgPiAkRklMRQ0KDQpzdWRvIHl1bSAteSBlcmFzZSBudHAqDQoNCnN1ZG8geXVtIC15IGluc3RhbGwgY2hyb255DQoNCnN1ZG8gc2VydmljZSBjaHJvbnlkIHN0YXJ0DQo=",
        instanceImage , "ecsInstanceRole");
    
  }

}
