package com.example.tabletop.store.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

<<<<<<< Updated upstream
import com.example.tabletop.image.entity.Image;
import com.example.tabletop.image.enums.ImageParentType;
import com.example.tabletop.image.service.ImageService;
import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.repository.SellerRepository;
=======
<<<<<<< Updated upstream
import com.example.tabletop.seller.service.SellerService;
=======
import com.example.tabletop.image.entity.Image;
import com.example.tabletop.image.enums.ImageParentType;
import com.example.tabletop.image.exception.ImageProcessingException;
import com.example.tabletop.image.service.ImageService;
import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.repository.SellerRepository;
>>>>>>> Stashed changes
>>>>>>> Stashed changes
import com.example.tabletop.store.dto.StoreDetailsDTO;
import com.example.tabletop.store.dto.StoreListResponseDTO;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.enums.StoreType;
import com.example.tabletop.store.repository.StoreRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
	@Value("${store.files.save.dir}")
	String saveDir;
	
	private final StoreRepository storeRepository;
	private final SellerRepository sellerRepository;
	private final ImageService imageService;
	
	// loginId에 해당하는 모든 가게 조회
	public List<StoreListResponseDTO> getStoreListByLoginId(String loginId) {
		log.info("Fetching stores for login id: {}", loginId);
		
		return storeRepository.findAllBySeller_LoginId(loginId)
				.stream()
				.map(entity -> entityToStoreListResponseDTO(entity))
				.collect(Collectors.toList());
	}
	
	// 사업자등록번호 중복 확인
	public boolean checkCorporateRegistrationNumberDuplication(String corporateRegistrationNumber) {
		log.info("Checking corporate registration number duplication: {}", corporateRegistrationNumber);
		
		return storeRepository.existsByCorporateRegistrationNumber(corporateRegistrationNumber);
	}
	
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
=======
>>>>>>> Stashed changes
	// loginId로 판매자를 조회하여 그 판매자에게 가게 등록
	public void insertStore(String loginId, 
							String name,
							String storeType,
							String corporateRegistrationNumber,
							String openDate,
							String closeDate,
							String description,
							String address,
							String notice,
							String openTime,
							String closeTime,
							String[] holidays,
							MultipartFile imageFile) {
		log.info("Creating new store for login id: {}", loginId);

		// 로그인 사용자 찾기
		Seller seller = sellerRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                	log.error("Seller not found with login_id: {}", loginId);
                	return new EntityNotFoundException("Seller not found with login_id: " + loginId);
                });
		
		// 시간 변환(String -> LocalTime)
		LocalTime parsedOpenTime = null;
        LocalTime parsedCloseTime = null;
		
		try {
            parsedOpenTime = LocalTime.parse(openTime, DateTimeFormatter.ISO_LOCAL_TIME);
            parsedCloseTime = LocalTime.parse(closeTime, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid format: " + parsedOpenTime);
            System.out.println("Invalid format: " + parsedCloseTime);
        }
		
		// String 배열을 Set<String>로 변환
		Set<String> holidaySet = Arrays.stream(holidays).collect(Collectors.toSet());
		
		// dto 객체 생성
		StoreDetailsDTO dto = null;
		
		if(storeType.equals(StoreType.ORDINARY.getName())) {
			dto = StoreDetailsDTO.builder()
								.name(name)
								.storeType(StoreType.ORDINARY)
								.corporateRegistrationNumber(corporateRegistrationNumber)
								.description(description)
								.address(address)
								.notice(notice)
								.openTime(parsedOpenTime)
								.closeTime(parsedCloseTime)
								.holidays(holidaySet)
								.sellerName(seller.getUsername())
								.build();
		} else if(storeType.equals(StoreType.TEMPORARY.getName())) {
			// 날짜 변환(String -> LocalDate)
	        LocalDate parsedOpenDate = null;
	        LocalDate parsedCloseDate = null;
			
			try {
	            parsedOpenDate = LocalDate.parse(openDate, DateTimeFormatter.ISO_LOCAL_DATE);
	            parsedCloseDate = LocalDate.parse(closeDate, DateTimeFormatter.ISO_LOCAL_DATE);
	        } catch (DateTimeParseException e) {
	            System.out.println("Invalid format: " + parsedOpenDate);
	            System.out.println("Invalid format: " + parsedCloseDate);
	        }
			
			dto = StoreDetailsDTO.builder()
								.name(name)
								.storeType(StoreType.TEMPORARY)
								.openDate(parsedOpenDate)
								.closeDate(parsedCloseDate)
								.description(description)
								.address(address)
								.notice(notice)
								.openTime(parsedOpenTime)
								.closeTime(parsedCloseTime)
								.holidays(holidaySet)
								.sellerName(seller.getUsername())
								.build();
		}

		// 이미지를 추가해야 하므로 바로 저장 필요
		Store storeEntity = storeRepository.saveAndFlush(StoreDetailsDTOToEntity(dto, seller.getId()));
		
		log.info("Created new store with id: {} for login id: {}", storeEntity.getStoreId(), loginId);
		
		if (imageFile != null && !imageFile.isEmpty()) {
<<<<<<< Updated upstream
            Image image = imageService.saveImage(imageFile, storeEntity.getStoreId(), ImageParentType.STORE);
            storeEntity.setImage(image);
            storeRepository.save(storeEntity);
=======
            Image imageEntity;
			try {
				imageEntity = imageService.saveImage(imageFile, storeEntity.getStoreId(), ImageParentType.STORE);
				storeEntity.setImage(imageEntity);
				storeRepository.save(storeEntity);
			} catch (ImageProcessingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
>>>>>>> Stashed changes
        }
		
	}
	
	// 가게 상세 정보 조회
	public StoreDetailsDTO getStoreDetails(Long storeId) {
		log.info("Fetching store with id: {}", storeId);
		
		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> {
					log.error("Store not found with id: {}", storeId);
					return new EntityNotFoundException("Store not found with id: " + storeId);
				});
		
		StoreDetailsDTO dto = entityToStoreDetailsDTO(store);
		
		if(store.getImage() != null) {			
			// 서버에 저장된 이미지 경로를 통해 base64파일로 변환하여 전달
			Path filepath =  Paths.get(store.getImage().getFilepath());
			if (Files.exists(filepath)) {
				byte[] fileBytes = null;
				try {
					fileBytes = Files.readAllBytes(filepath);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String base64File = Base64.getEncoder().encodeToString(fileBytes);
				dto.setImageBase64(base64File);
			}
		}
		
		return dto;
	}
	
<<<<<<< Updated upstream
=======
>>>>>>> Stashed changes
>>>>>>> Stashed changes
	// 가게 수정
	@Transactional
	public void updateStoreByStoreId(Long storeId,
									String name,
									String description,
									String address,
									String notice,
									String openTime,
									String closeTime,
									String[] holidays,
									MultipartFile imageFile) {
		log.info("Updating store with id: {} ", storeId);
		
		// 시간 변환(String -> LocalTime)
		LocalTime parsedOpenTime = null;
        LocalTime parsedCloseTime = null;
		
		try {
            parsedOpenTime = LocalTime.parse(openTime, DateTimeFormatter.ISO_LOCAL_TIME);
            parsedCloseTime = LocalTime.parse(closeTime, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid format: " + parsedOpenTime);
            System.out.println("Invalid format: " + parsedCloseTime);
        }
		
		// String 배열을 Set<String>로 변환
		Set<String> holidaySet = Arrays.stream(holidays).collect(Collectors.toSet());
		
		// 가게 찾기
		Store storeEntity = storeRepository.findById(storeId)
			.orElseThrow(() -> {
				log.error("Store not found with id: {}", storeId);
				return new EntityNotFoundException("Store not found with id: " + storeId);
			});
		
		storeEntity.updateDetails(name,
								description,
								address,
								notice,
								parsedOpenTime,
								parsedCloseTime,
								holidaySet);
		
<<<<<<< Updated upstream
		// 이미지 변경 - 삭제되어 없거나 바뀌었거나,,, 삭제 어떻게????
		if (imageFile != null && !imageFile.isEmpty()) {
            Image image = imageService.saveImage(imageFile, storeEntity.getStoreId(), ImageParentType.STORE);
            storeEntity.setImage(image);
        }
=======
<<<<<<< Updated upstream
		// 이미지 변경 구현 필요
=======
		// 이미지 변경 - 삭제되어 없거나 바뀌었거나,,, 삭제 어떻게????
		if (imageFile != null && !imageFile.isEmpty()) {
            Image imageEntity;
			try {
				imageEntity = imageService.saveImage(imageFile, storeEntity.getStoreId(), ImageParentType.STORE);
				storeEntity.setImage(imageEntity);
			} catch (ImageProcessingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
>>>>>>> Stashed changes
>>>>>>> Stashed changes
		
		storeRepository.save(storeEntity);
		log.info("Updated store with id: {}", storeId);
	}
	
	// 가게 삭제
	public void deleteStoreByStoreId(Long storeId) {
		log.info("Deleting store with id: {}", storeId);
		
		Store storeEntity = storeRepository.findById(storeId)
				.orElseThrow(() -> {
					log.error("Store not found with id: {}", storeId);
					return new EntityNotFoundException("Store not found with id: " + storeId);
				});
		
		// 가게의 이미지파일 폴더 삭제(가게 이미지 + 메뉴 이미지)
		String storeDir = saveDir + File.separator + storeId.toString();
		File directory = new File(storeDir);
		if (directory.exists()) {
			File[] files = directory.listFiles();
			
			for(File file: files) {
				file.delete();
			}
				
			directory.delete();
		}

		// store의 레코드 삭제 시, cascade로 image, menu, orders(?) 레코드 같이 삭제
		storeRepository.delete(storeEntity);
		
		log.info("Deleted menu with id: {}", storeId);
	}
		
	// Entity -> StoreListResponseDTO
	public StoreListResponseDTO entityToStoreListResponseDTO(Store entity) {
		
		StoreListResponseDTO dto = StoreListResponseDTO.builder()
							.storeId(entity.getStoreId())
							.name(entity.getName())
							.storeType(entity.getStoreType())
							.build();
		
		return dto;
	}
	
	// Entity -> StoreDetailsDTO
	public StoreDetailsDTO entityToStoreDetailsDTO(Store entity) {
		
		StoreDetailsDTO dto = StoreDetailsDTO.builder()
							.storeId(entity.getStoreId())
							.name(entity.getName())
							.storeType(entity.getStoreType())
							.corporateRegistrationNumber(entity.getCorporateRegistrationNumber())
							.openDate(entity.getOpenDate())
							.closeDate(entity.getCloseDate())
							.description(entity.getDescription())
							.address(entity.getAddress())
							.notice(entity.getNotice())
							.openTime(entity.getOpenTime())
							.closeTime(entity.getCloseTime())
							.holidays(entity.getHolidays())
							.sellerName(entity.getSeller().getUsername())
							.build();
		
		return dto;
	}
		
	// StoreDetailsDTO -> Entity
	public Store StoreDetailsDTOToEntity(StoreDetailsDTO dto, Long sellerId) {
		Seller seller = sellerRepository.findById(sellerId)
	            .orElseThrow(() -> new EntityNotFoundException("Seller not found with id: " + sellerId));
		
		Store entity = Store.builder()
							.name(dto.getName())
							.storeType(dto.getStoreType())
							.corporateRegistrationNumber(dto.getCorporateRegistrationNumber())
							.openDate(dto.getOpenDate())
							.closeDate(dto.getCloseDate())
							.description(dto.getDescription())
							.address(dto.getAddress())
							.notice(dto.getNotice())
							.openTime(dto.getOpenTime())
							.closeTime(dto.getCloseTime())
							.holidays(dto.getHolidays())
							.seller(seller)
							.build();
		
		return entity;
	}
}