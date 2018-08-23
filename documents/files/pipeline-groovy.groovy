node('maven') {
  stage 'build & deploy in dev'
  openshiftBuild(namespace: 'development',
  			    buildConfig: 'cdnapi',
			    showBuildLogs: 'true',
			    waitTime: '3000000')
  
  stage 'verify deploy in dev'
  openshiftVerifyDeployment(namespace: 'development',
				       depCfg: 'cdnapi',
				       replicaCount:'1',
				       verifyReplicaCount: 'true',
				       waitTime: '300000')
  
  stage 'deploy in test'
  openshiftTag(namespace: 'development',
  			  sourceStream: 'cdnapi',
			  sourceTag: 'latest',
			  destinationStream: 'cdnapi',
			  destinationTag: 'promoteQA')
  
  openshiftDeploy(namespace: 'testing',
  			     deploymentConfig: 'cdnapi',
			     waitTime: '300000')

  openshiftScale(namespace: 'testing',
  			     deploymentConfig: 'cdnapi',
			     waitTime: '300000',
			     replicaCount: '2')
  
  stage 'verify deploy in test'
  openshiftVerifyDeployment(namespace: 'testing',
				       depCfg: 'cdnapi',
				       replicaCount:'2',
				       verifyReplicaCount: 'true',
				       waitTime: '300000')
  
  stage 'Deploy to production'
  timeout(time: 2, unit: 'DAYS') {
      input message: 'Approve to production?'
 }

  openshiftTag(namespace: 'development',
  			  sourceStream: 'cdnapi',
			  sourceTag: 'promoteQA',
			  destinationStream: 'cdnapi',
			  destinationTag: 'promotePRD')

  
  openshiftDeploy(namespace: 'production',
  			     deploymentConfig: 'cdnapi',
			     waitTime: '300000')
  
  openshiftScale(namespace: 'production',
  			     deploymentConfig: 'cdnapi',
			     waitTime: '300000',
			     replicaCount: '2')
  
  stage 'verify deploy in production'
  openshiftVerifyDeployment(namespace: 'production',
				       depCfg: 'cdnapi',
				       replicaCount:'2',
				       verifyReplicaCount: 'true',
				       waitTime: '300000')
}
