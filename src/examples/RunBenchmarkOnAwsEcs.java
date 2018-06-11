package examples;

import java.io.File;
import tools.descartes.bungee.cloud.aws.ecs.management.AwsEcsManagement;
import tools.descartes.bungee.cloud.aws.ecs.services.AwsEcsService;
import tools.descartes.bungee.examples.RunBenchmark;
import tools.descartes.bungee.utils.FileUtility;

/**
 * Executes the BUNGEE benchmark for ECS.
 * This class is based on the original BUNGEE example "RunBenchmarkOnAWS.java"
 * @author nora
 *
 */
public class RunBenchmarkOnAwsEcs {

  
  private static File fileLocation = FileUtility.FILE_LOCATION;
  // create classes that handle all ECS functionality
  private static AwsEcsService EcsService = new AwsEcsService("BungeeCluster", "BungeeService");
  private static AwsEcsManagement cloud = new AwsEcsManagement(EcsService);

  public static void main(String[] args) {
    
    // configure and run benchmark
    File measurementConfigFile = new File(fileLocation, "propertyFiles/measurement.prop");
    RunBenchmark.runBenchmarkManagedCloud(cloud, measurementConfigFile, 1);

  }

}
