package tools.descartes.bungee.cloud.aws.ecs.management;

import com.amazonaws.services.ecs.model.UpdateServiceResult;
import tools.descartes.bungee.cloud.Bounds;
import tools.descartes.bungee.cloud.CloudInfo;
import tools.descartes.bungee.cloud.CloudManagement;
import tools.descartes.bungee.cloud.aws.ecs.services.AwsEcsService;

/**
 * Enables the benchmark to get and set the desired 
 * task count in AWS ECS and get the number 
 * of tasks currently running. 
 * @author nora
 *
 */
public class AwsEcsManagement implements CloudInfo, CloudManagement {

  AwsEcsService service;
  
  /**
   * Constructor instantiating an object of the class
   * AwsEcsService, to handle all needed operations. 
   * @param service
   */
  public AwsEcsManagement(AwsEcsService service) {
    super();
    this.service = service;
  }

  /**
   * Get the "desired task count of the ECS service.
   */
  @Override
  public Bounds getScalingBounds(String hostName) {
    System.out.println("Ecs Managment getting scaling bounds");
    Bounds bounds = new Bounds(1, service.getDesiredCount());
    return bounds;
  }

  /**
   * Change the desired task count of the ECS service.
   */
  @Override
  public boolean setScalingBounds(String hostName, Bounds bounds) {
    System.out.println("Ecs Managment setting scaling bounds");
    UpdateServiceResult result = service.updateEcsServiceDesiredCount(bounds.getMax());
    if (result != null) {
      return true;
    }
    return false;
  }

  @Override
  /**
   * Get the number of running tasks in the ECS service
   */
  public int getNumberOfResources(String ip) {
    //System.out.println("Ecs Management getting number of resources");
    int numberOfResources = service.getRunningCount();
    return numberOfResources;
  }

}
