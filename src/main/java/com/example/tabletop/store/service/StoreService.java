package com.example.tabletop.store.service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.repository.SellerRepository;
import com.example.tabletop.store.dto.StoreDetailsDTO;
import com.example.tabletop.store.dto.StoreListResponseDTO;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.enums.StoreType;
import com.example.tabletop.store.repository.StoreRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {
	@Value("${store.files.save.path}")
	String savePath;
	
	private final StoreRepository storeRepository;
	private final SellerRepository sellerRepository;
//	private final SellerService sellerService;
	
	
	// loginId에 해당하는 모든 가게 조회
	public List<StoreListResponseDTO> getStoreListByLoginId(String loginId) {
		
		return storeRepository.findAllBySeller_LoginId(loginId)
				.stream()
				.map(entity -> entityToStoreListResponseDTO(entity))
				.collect(Collectors.toList());
	}
	
	// 사업자등록번호 중복 확인
	public boolean checkCorporateRegistrationNumberDuplication(String corporateRegistrationNumber) {
		return storeRepository.existsByCorporateRegistrationNumber(corporateRegistrationNumber);
	}
	
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
							String holidays,
							MultipartFile image) {
		
		// 로그인 사용자 찾기
		Seller seller = sellerRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with login_id: " + loginId));
		
		// 날짜, 시간 변환(String -> LocalDateTime)
		LocalTime parsedOpenTime = null;
        LocalTime parsedCloseTime = null;
        LocalDate parsedOpenDate = null;
        LocalDate parsedCloseDate = null;
		
		try {
            parsedOpenTime = LocalTime.parse(openTime, DateTimeFormatter.ISO_LOCAL_TIME);
            parsedCloseTime = LocalTime.parse(closeTime, DateTimeFormatter.ISO_LOCAL_TIME);
            parsedOpenDate = LocalDate.parse(openDate, DateTimeFormatter.ISO_LOCAL_DATE);
            parsedCloseDate = LocalDate.parse(closeDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid format: " + parsedOpenTime);
            System.out.println("Invalid format: " + parsedCloseTime);
            System.out.println("Invalid format: " + parsedOpenDate);
            System.out.println("Invalid format: " + parsedCloseDate);
        }
		
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
								.holidays(null)
								.seller(seller)
								.build();
		} else if(storeType.equals(StoreType.TEMPORARY.getName())) {
			dto = StoreDetailsDTO.builder()
								.name(name)
								.storeType(StoreType.TEMPORARY)
								.openDate(null)
								.closeDate(null)
								.description(description)
								.address(address)
								.notice(notice)
								.openTime(parsedOpenTime)
								.closeTime(parsedCloseTime)
								.holidays(null)
								.seller(seller)
								.build();
		}

		// 이미지 추가 구현 필요
		
		storeRepository.save(StoreDetailsDTOToEntity(dto));
	}
	
	// 가게 수정
	@Transactional
	public void updateStoreByStoreId(Long storeId,
									String name,
									String description,
									String address,
									String notice,
									String openTime,
									String closeTime,
									String holidays,
									MultipartFile image) {
		
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
		
		// 가게 찾기
		Store storeEntity = storeRepository.findById(storeId)
			.orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + storeId));
		
//		storeEntity.updateDetails(name,
//								description,
//								address,
//								notice,
//								parsedOpenTime,
//								parsedCloseTime,
//								holidays);
		
		// 이미지 변경 구현 필요, 삭제되어 없거나 바뀌었거나
		storeEntity.setImage(null);
		
		storeRepository.save(storeEntity);
	}
	
	// 가게 삭제(로그인된 판매자의 비밀번호와 일치해야 삭제 가능)
	public void deleteStoreByStoreId(Long storeId, String password) {
		// 비밀번호 확인
//		if (!userService.verifyPassword(userId, password)) {
//			// throw new AccessDeniedException("Invalid password");
//        }
		
		Store storeEntity = storeRepository.findById(storeId)
				.orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + storeId));
		
		// 가게의 이미지파일 폴더 삭제(가게 이미지 + 메뉴 이미지)
		String storeDir = savePath + File.separator + storeId.toString();
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
	}
	
	// 파일 경로를 통해서 파일 찾아오기
//	public ProjectfileDTO getProjectfileByFilePath(String fullPath) {
//		List<String> fileInfo = extractPathAndName(fullPath);
//		Projectfile entity = projectfileRepository.findByFilePathAndFileName(fileInfo.get(0), fileInfo.get(1));
//
//		return entityToDto(entity);
//	}
	
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
//								.storeId(entity.getStoreId())
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
//								.seller(entity.getSeller())
								.build();
			
			return dto;
		}
		
	// StoreDetailsDTO -> Entity
	public Store StoreDetailsDTOToEntity(StoreDetailsDTO dto) {
		
		Store entity = Store.builder()
//							.storeId(dto.getStoreId())
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
							.seller(dto.getSeller())
							.build();
		
		return entity;
	}
}