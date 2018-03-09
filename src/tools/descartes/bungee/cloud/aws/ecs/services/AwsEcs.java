package tools.descartes.bungee.cloud.aws.ecs.services;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSAsyncClientBuilder;

public class AwsEcs {
  
  private AmazonECS ecs = AmazonECSAsyncClientBuilder.standard().build();
  
  /*
   * Constructor
   */
  public AwsEcs() {
    // TODO Auto-generated constructor stub
  }
  
  /*
   * Getters and setters
   */
  
  public AmazonECS getEcs() {
    return ecs;
  }

  public void setEcs(AmazonECS ecs) {
    this.ecs = ecs;
  }

}
