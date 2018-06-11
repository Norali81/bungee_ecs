# BUNGEE ECS extension - Installation and setup

## Load driver

* Install [Eclipse for RCP and RAP developers](http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/oxygen2).
* Import the BUNGEE and LIMBO code from gitHub according to the instructions in the BUNGEE [quickstart guide](https://se.informatik.uni-wuerzburg.de/fileadmin/10030200/BUNGEE-HowTo.pdf).
* Install the AWS Eclipse toolkit according to the instructions in the quickstart guide. If this leads to an error, you can download the [AWS Java SDK](https://aws.amazon.com/sdk-for-java/) and add it to your Eclipse project as a user defined library. 
* Download Apache Jmeter according to the instructions in the quickstart guide. 
* Import the BUNGEE ECS extension[BUNGEE ECS extension](https://github.com/Norali81/bungee_ecs)  from GitHub. (This extension will be publically avaialble once the Master's thesis has been graded).


## AWS account
#### Ensure AWS SDK can access account
* Create a user in [AWS IAM](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html) .
* Give desired permissions to user. 
* Ensure the user's credentials are accessible to the JAVA SDK following the [AWS instructions](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html) . 
* Ensure the default region is set. [Instructions](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html).
 * Create an EC2 private key following [these](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html#having-ec2-create-your-key-pair) instructions.
 * Store the private key (.pem) in your ~/.ssh directory. This will only be used in case you have to ssh into one of your EC2 instances for troubleshooting. 
 * Take note of the name of this newly created key. 

#### Create AWS IAM roles
Create the following roles (instructions [here](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create_for-service.html) here):

Role name | Policy
---------------| ---------------
ecsServiceRole | AmazonEC2ContainerServiceRole
ecsInstanceRole | AmazonEC2ContainerServiceforEC2Role, AmazonS3ReadOnlyAccess
ecsTaskRole | AmazonECSTaskExecutionRolePolicy
ecsAutoscaleRole | AmazonEC2ContainerServiceAutoscaleRole

Take note of the ARN of the ecsServiceRole.

### Upload load processor application to ECR
* Put your load processing application inside a Docker container and ensure it runs correctly. 
* Upload your container image to the Elastic Container Registry following AWS's [instructions](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html) instructions. 
* Take note of the Container Registry URL pointing to your uploaded docker image. 

## Configure BUNGEE

#### Setup script
* Eclipse: In the package "tools.descartes.bungee.cloud.aws.ecs.management" open the file "SetUpEnviornment.java".
* Replace the following variables with those matching your own account: 
	* Subnet 1 - 3 &rarr; Replace with your subnet IDs.
	* containerRegistryUrl &rarr; Replace with the URL obtained in a previous step. 
	* ecsServiceRoleArn &rarr;  Replace with the ARN of the ecsServiceRole you noted down earlier.
	* awsKeyName &rarr;  Replace with the name of the AWSKey you noted down earlier. 
	* instanceType &rarr;  Replace with the name of the instance type you want for your EC2 instances.
	* instanceImages &rarr; Replace with the current [ECS optimised image](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ecs-optimized_AMI.html)  for the AWS region you are creating your experiment in (established in an earlier step). 
	* Set numberOfDesiredTasks to the number of desired tasks you would like. 
	* Set numberOfInstances to the number of EC2 instances you would like to create. 
* Execute SetupEnviornment.java

The script should now create  the following: 

* Security groups for instances and load balancer.
* ECS cluster called "BungeeCluster".
* An EC2 application load balancer.
* A target group within EC2.
* Create an ECS service called bungeeService.
* Create a container definition with 400 CPU units reservation. 
* Create a task definition with  with 450 CPU units reserved and 450MB of RAM. 
* Create the number of EC2 instances specified with the variable numberOfInstances.

## Configure BUNGEE
Configure BUNGEE by editing the property files according to the instructions in the [quickstart guide](https://se.informatik.uni-wuerzburg.de/fileadmin/10030200/BUNGEE-HowTo.pdf):

* Replace the hostname in  the file "host.prop" with the hostname of the application load balancer that was created in your account. 
* Ensure the JMeter path is correct in jmeter.prop.
* Adjust request.prop and measurement.prop as needed. 

Now you should be all set for running the system analysis phase. Once this phase is concluded, you need to set up autoscaling for the ECS service and the EC2 instances. Refer to AWS's documentation for those steps. 





 