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


/**
 * Contains an AmazonEC2ClientBuilder. 
 * The purpose of the class is to hide the complexity of using the clientBuilder
 * And allowing operations on the client builder. 
 * 
 * @author nora
 *
 */
public class Ec2 {

  private AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().build();

  /**
   * Constructor
   */
  public Ec2() {
    
  }

  /**
   * Runs an EC2 instance. This method is currently not used anywhere. 
   * @param instanceType
   * @param keyPair
   * @param securityGroup
   * @param iamInstanceProfile
   * @param userData
   * @return runInstancesResult
   * @throws Exception
   */
 /* public RunInstancesResult runEc2Instance(String instanceType, String keyPair,
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

*/

}

