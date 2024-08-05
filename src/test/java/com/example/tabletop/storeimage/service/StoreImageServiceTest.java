package com.example.tabletop.storeimage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.tabletop.storeimage.entity.StoreImage;
import com.example.tabletop.storeimage.repository.StoreImageRepository;

class StoreImageServiceTest {

    @Mock
    private StoreImageRepository storeImageRepository;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private StoreImageService storeImageService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveImage() throws Exception {
        // given
        Long storeId = 1L;
        String originalFilename = "test.jpg";
        String filepath = "C:\\tabletop";
        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(storeImageRepository.save(any(StoreImage.class))).thenAnswer(i -> {
            StoreImage savedImage = (StoreImage) i.getArguments()[0];
            return new StoreImage(savedImage.getFilename(), savedImage.getFileOriginalName(), 
                                  savedImage.getFilepath(), savedImage.getS3Url());
        });

        // when
        StoreImage result = storeImageService.saveImage(storeId, multipartFile);

        // then
        assertNotNull(result);
        assertEquals(originalFilename, result.getFileOriginalName());
        assertEquals(filepath, result.getFilepath());
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(storeImageRepository, times(1)).save(any(StoreImage.class));
    }

    @Test
    void testDeleteFolderFromS3() throws Exception {
        // given
        Long storeId = 1L;
        ListObjectsV2Result listObjectsV2Result = new ListObjectsV2Result();
        S3ObjectSummary objectSummary = new S3ObjectSummary();
        objectSummary.setKey("test/key");
        List<S3ObjectSummary> objectSummaries = new ArrayList<>();
        objectSummaries.add(objectSummary);
        
        when(amazonS3.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Result);
        when(listObjectsV2Result.getObjectSummaries()).thenReturn(objectSummaries);
        when(listObjectsV2Result.isTruncated()).thenReturn(false);

        // when
        storeImageService.deleteFolderFromS3(storeId);

        // then
        verify(amazonS3, times(1)).listObjectsV2(any(ListObjectsV2Request.class));
        verify(amazonS3, times(1)).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    void testDeleteImageFromS3() throws Exception {
        // given
        Long imageId = 1L;
        StoreImage storeImage = new StoreImage("uuid_test.jpg", "test.jpg", "C:\\tabletop", "test/image.jpg");
        when(storeImageRepository.findById(imageId)).thenReturn(Optional.of(storeImage));

        // when
        storeImageService.deleteImageFromS3(imageId);

        // then
        verify(amazonS3, times(1)).deleteObject(any(DeleteObjectRequest.class));
        verify(storeImageRepository, times(1)).delete(any(StoreImage.class));
    }
}