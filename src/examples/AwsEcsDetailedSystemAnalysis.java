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

public class AwsEcsDetailedSystemAnalysis {

  public static void main(String[] args) {
    
    File jmeterPropertiesFile   = new File(FileUtility.FILE_LOCATION, "propertyFiles/jmeter.prop");
    File hostPropertiesFile   = new File(FileUtility.FILE_LOCATION, "propertyFiles/host.prop");
    File requestPropertiesFile  = new File(FileUtility.FILE_LOCATION, "propertyFiles/request.prop");
    
    int maxResources = 6;
    double percent = 95;
    double responseTime = 500;

    ServiceLevelObjective slo = new SuccessRateSLO(percent, responseTime);

    JMeterController jMeter = new JMeterController(jmeterPropertiesFile);
    Request request = Request.load(requestPropertiesFile);
    Host host = Host.load(hostPropertiesFile);
    AwsEcsService EcsService = new AwsEcsService("HelloWorld", "TestService");
    AwsEcsManagement cloudManagement = new AwsEcsManagement(EcsService);
    
    //SystemAnalysis analysis = new SimpleSystemAnalysis(jMeter);
    SystemAnalysis analysis = new DetailedSystemAnalysis(jMeter, cloudManagement);
    
    
    analysis.setMaxResources(maxResources);
    analysis.analyzeSystem(host, request, slo);
  }

}
