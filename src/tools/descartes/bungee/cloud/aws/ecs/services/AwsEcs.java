package tools.descartes.bungee.cloud.aws.ecs.services;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSAsyncClientBuilder;

/**
 * This class does nothing else but creating an AmazonECSAsyncClientBuilder.
 * The class into a separate class to hide the complexity of 
 * using the client builder.
 * @author nora
 *
 */
public class AwsEcs {
  
  private AmazonECS ecs = AmazonECSAsyncClientBuilder.standard().build();
  
  /*
   * Constructor
   */
  public AwsEcs() {
   
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
