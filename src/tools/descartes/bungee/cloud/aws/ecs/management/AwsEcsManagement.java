package tools.descartes.bungee.cloud.aws.ecs.management;

import com.amazonaws.services.ecs.model.UpdateServiceResult;
import tools.descartes.bungee.cloud.Bounds;
import tools.descartes.bungee.cloud.CloudInfo;
import tools.descartes.bungee.cloud.CloudManagement;
import tools.descartes.bungee.cloud.aws.ecs.services.AwsEcsService;

public class AwsEcsManagement implements CloudInfo, CloudManagement {

  AwsEcsService service;
  
  public AwsEcsManagement(AwsEcsService service) {
    super();
    this.service = service;
  }

  @Override
  public Bounds getScalingBounds(String hostName) {
    System.out.println("Ecs Managment getting scaling bounds");
    Bounds bounds = new Bounds(1, service.getDesiredCount());
    return bounds;
  }

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
  public int getNumberOfResources(String ip) {
    //System.out.println("Ecs Managment getting number of resources");
    int numberOfResources = service.getRunningCount();
    return numberOfResources;
  }

}
