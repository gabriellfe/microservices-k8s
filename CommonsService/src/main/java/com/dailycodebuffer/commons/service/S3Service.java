package com.dailycodebuffer.commons.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@Service
@ConditionalOnProperty(value = "enable.s3", havingValue = "true", matchIfMissing = false)
public class S3Service {

	private final String BUCKET = "front-end-gabriell";

	@Autowired
	private AmazonS3 clientS3;

	public void putObject(byte[] item, String name, String type) {
		System.out.println(name);
		InputStream targetStream = new ByteArrayInputStream(item);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(type);
		metadata.setContentLength(item.length);
		PutObjectRequest por = new PutObjectRequest(BUCKET, name, targetStream, metadata);
		clientS3.putObject(por.withCannedAcl(CannedAccessControlList.PublicRead));
	}

	public File downloadObject(String bucketName, String objectName) throws Exception {
		S3Object s3object = clientS3.getObject(bucketName, objectName);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		File file = new File("." + File.separator + objectName);
		try {
			FileUtils.copyInputStreamToFile(inputStream, file);
		} catch (IOException e) {
		}
		return file;

	}
	
	public File downloadObject( String objectName) throws Exception {
		S3Object s3object = clientS3.getObject(BUCKET, objectName);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		File file = new File("." + File.separator + objectName);
		try {
			FileUtils.copyInputStreamToFile(inputStream, file);
		} catch (IOException e) {
		}
		return file;

	}

}