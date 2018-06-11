
package examples;

import java.io.File;
import tools.descartes.bungee.analysis.DetailedSystemAnalysis;
import tools.descartes.bungee.analysis.SystemAnalysis;
import tools.descartes.bungee.cloud.aws.ecs.management.AwsEcsManagement;
import tools.descartes.bungee.cloud.aws.ecs.services.AwsEcsService;
import tools.descartes.bungee.config.Host;
import tools.descartes.bungee.config.Request;
import tools.descartes.bungee.loadgeneration.JMeterController;
import tools.descartes.bungee.slo.ServiceLevelObjective;
import tools.descartes.bungee.slo.SuccessRateSLO;
import tools.descartes.bungee.utils.FileUtility;
/**
 * Executes the BUNGEE System Analysis for ECS.
 * 
 * This class is based on the original BUNGEE example "DetailedAWSAnalysis.java"
 * @author nora
 *
 */
public class AwsEcsDetailedSystemAnalysis {


  public static void main(String[] args) {
    
    /*
     * Path to property files.
     * Ensure to update those files with your desired values.
     */
    File jmeterPropertiesFile   = new File(FileUtility.FILE_LOCATION, "propertyFiles/jmeter.prop");
    File hostPropertiesFile   = new File(FileUtility.FILE_LOCATION, "propertyFiles/host.prop");
    File requestPropertiesFile  = new File(FileUtility.FILE_LOCATION, "propertyFiles/request.prop");
    
    // Maximum resource number to evaluate during system analysis
    int maxResources = 6;
    // Percent successful requests required for meeting SLO
    double percent = 95;
    // Response time SLO
    double responseTime = 500;

    ServiceLevelObjective slo = new SuccessRateSLO(percent, responseTime);

    // Create jMeter Controller, request and host
    JMeterController jMeter = new JMeterController(jmeterPropertiesFile);
    Request request = Request.load(requestPropertiesFile);
    Host host = Host.load(hostPropertiesFile);
    
    // Create necessary classes for system analysis to run
    AwsEcsService EcsService = new AwsEcsService("BungeeCluster", "BungeeService");
    AwsEcsManagement cloudManagement = new AwsEcsManagement(EcsService);
    
    //Create system analysis class
    SystemAnalysis analysis = new DetailedSystemAnalysis(jMeter, cloudManagement);
    
    analysis.setMaxResources(maxResources);
    // run system analysis
    analysis.analyzeSystem(host, request, slo);
  }

}
