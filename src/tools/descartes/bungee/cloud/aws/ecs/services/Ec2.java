package tools.descartes.bungee.cloud.aws.ecs.services;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.IamInstanceProfile;
import com.amazonaws.services.ec2.model.IamInstanceProfileSpecification;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagSpecification;
import com.amazonaws.services.ec2.model.ResourceType;



public class Ec2 {

  private AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().build();

  public Ec2() {
    
  }


  public RunInstancesResult runEc2Instance(String instanceType, String keyPair,
      String securityGroup, String iamInstanceProfile, String userData) throws Exception {

    try {
      RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

      runInstancesRequest.withImageId("ami-0693ed7f") // ECS optimized image for eu-west-1
          .withInstanceType(instanceType)
          .withMinCount(1)
          .withMaxCount(1)
          .withKeyName(keyPair)
          .withSecurityGroups(securityGroup)
          .withTagSpecifications(new TagSpecification()
              .withTags(new Tag().withValue("BungeeServiceInstance").withKey("Name"))
              .withResourceType(ResourceType.Instance))
          .withIamInstanceProfile(new IamInstanceProfileSpecification().withName(iamInstanceProfile) // ecsInstanceRole
          )
          .withUserData(userData);
      RunInstancesResult result = this.ec2.runInstances(runInstancesRequest);
      return result;
    } catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
      return null;
    }
  }



}

