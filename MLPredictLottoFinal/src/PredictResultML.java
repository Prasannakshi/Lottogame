import java.util.List;
import java.util.concurrent.Future;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.machinelearning.AmazonMachineLearningAsyncClient;
import com.amazonaws.services.machinelearning.model.CreateBatchPredictionRequest;
import com.amazonaws.services.machinelearning.model.CreateBatchPredictionResult;
import com.amazonaws.services.machinelearning.model.GetBatchPredictionRequest;
import com.amazonaws.services.machinelearning.model.GetBatchPredictionResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class PredictResultML {

	static String k = null;

	public static void main(String[] args) {

		CreateBatchPredictionRequest req = new CreateBatchPredictionRequest();
		String id = "Predlotto14";
		req.setBatchPredictionId(id);
		req.setBatchPredictionDataSourceId("ds-P94Cl6k2Vqn");
		req.setMLModelId("ml-frlNt1UPZxT");
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (C:\\Users\\Prasannakshi\\.aws\\credentials), and is in valid format.",
							e);
		}
		String bucketName = "lottotestresult";
		AmazonS3 s3 = new AmazonS3Client(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2); 
	    System.out.println("Given Bucket " + bucketName + "\n");
		ObjectListing l = s3.listObjects(bucketName);
		List<S3ObjectSummary> summaries = l.getObjectSummaries();
		//System.out.println("summaries " + summaries + "\n");
		String key = null;
		for(S3ObjectSummary s : summaries)  {
			if(s.getBucketName().equals(bucketName))  {
		String outputURI = "s3://" + bucketName;
		req.setOutputUri(outputURI);
		req.setBatchPredictionName("LottoPredict12");
		req.setRequestCredentials(credentials);
		}
	}
		//		AmazonMachineLearningClient aml = new AmazonMachineLearningClient();

		AmazonMachineLearningAsyncClient aml = new AmazonMachineLearningAsyncClient();
		Future<CreateBatchPredictionResult> res = aml.createBatchPredictionAsync(req, new AsyncHandler<CreateBatchPredictionRequest, CreateBatchPredictionResult>() {

			@Override
			public void onSuccess(CreateBatchPredictionRequest request, CreateBatchPredictionResult result) {
				System.out.println("Success " + result.getBatchPredictionId());


				GetBatchPredictionRequest breq = new GetBatchPredictionRequest();
				breq.setBatchPredictionId(result.getBatchPredictionId());
				GetBatchPredictionResult  batchPredResult = aml.getBatchPrediction(breq);

				System.out.println("Output : " + batchPredResult.getOutputUri());
				System.out.println("Output : " + batchPredResult.getStatus());	
			}
//				while(true) 
//				{
//					System.out.println(" status : " + batchPredResult.getStatus());
//					if("Completed".equalsIgnoreCase(batchPredResult.getStatus())) {
//						System.out.println("done!!");
//						break;
//					}
//				}
//				  S3Object object = s3.getObject(new GetObjectRequest(bucketName, k));
//			      System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
//			      System.out.println(object.getObjectContent());
			

			@Override
			public void onError(Exception exception) {
				System.out.println("error " + exception.getMessage());

			}
		}); 	
		System.out.println("Finally done!");
//		readFile();		
	}
}
