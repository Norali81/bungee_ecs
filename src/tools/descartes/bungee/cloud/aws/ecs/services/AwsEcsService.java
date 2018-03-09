package tools.descartes.bungee.cloud.aws.ecs.services;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSAsyncClientBuilder;
import com.amazonaws.services.ecs.model.DescribeServicesRequest;
import com.amazonaws.services.ecs.model.DescribeServicesResult;
import com.amazonaws.services.ecs.model.UpdateServiceRequest;
import com.amazonaws.services.ecs.model.UpdateServiceResult;

public class AwsEcsService {
  
  private AwsEcs ecs = new AwsEcs();
  private String ecsClusterName;
  private String ecsServiceName;
  
  /*
   * Constructor
   */
  
  public AwsEcsService(String ecsClusterName, String ecsServiceName) {
    super();
    this.ecsClusterName = ecsClusterName;
    this.ecsServiceName = ecsServiceName;
  }
  
  
  private DescribeServicesResult describeEcsService() {
      DescribeServicesRequest request = new DescribeServicesRequest()
          .withCluster(this.ecsClusterName)
          .withServices(this.ecsServiceName);
      DescribeServicesResult result = this.ecs.getEcs().describeServices(request);
      return result;
  }
  
 
  
  /*
   * System.out.println("Array lenght" + describeResult.getServices().size());
    System.out.println("Status: " + describeResult.getServices().get(0).getStatus());
    System.out.println("ServiceName: " + describeResult.getServices().get(0).getServiceName());
    System.out.println("RunningCount: " + describeResult.getServices().get(0).getRunningCount());
    System.out.println("PendingCount: " + describeResult.getServices().get(0).getPendingCount());
    System.out.println("DesiredCount: " + describeResult.getServices().get(0).getDesiredCount());
    System.out.println("GetEvents: " + describeResult.getServices().get(0).getEvents());
   */
  public int getDesiredCount() {
    DescribeServicesResult result = describeEcsService();
    if (result.getServices().size() > 1) {
      System.out
          .println("Number of services meeting the input criteria > 1. Something went wrong!");
      System.exit(0);
    }

    return result.getServices()
        .get(0)
        .getDesiredCount();
  }

  public int getRunningCount() {

    try {
      DescribeServicesResult result = describeEcsService();

      if (result.getServices()
          .size() > 1) {
        throw new IllegalArgumentException(
            "Number of services meeting the input criteria > 1. Something went wrong!");
      }

      return result.getServices()
          .get(0)
          .getRunningCount();

    } catch (IllegalArgumentException e) {
      System.out.print(e);
    }
    return -1;
  }


  public UpdateServiceResult updateEcsServiceDesiredCount(int desiredCount) {
    UpdateServiceRequest request = new UpdateServiceRequest().withDesiredCount(desiredCount)
        .withCluster(this.ecsClusterName)
        .withService(this.ecsServiceName);
    UpdateServiceResult result = this.ecs.getEcs()
        .updateService(request);
    return result;
  }

  /*
   * Getters and Setters
   */
  public AwsEcs getEcs() {
    return ecs;
  }


  public void setEcs(AwsEcs ecs) {
    this.ecs = ecs;
  }


  public String getEcsClusterName() {
    return ecsClusterName;
  }


  public void setEcsClusterName(String ecsClusterName) {
    this.ecsClusterName = ecsClusterName;
  }


  public String getEcsServiceName() {
    return ecsServiceName;
  }


  public void setEcsServiceName(String ecsServiceName) {
    this.ecsServiceName = ecsServiceName;
  }

}
