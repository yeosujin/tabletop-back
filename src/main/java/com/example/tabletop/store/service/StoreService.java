package com.example.tabletop.store.service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import com.example.tabletop.store.dto.StoreDetailsDTO;
import com.example.tabletop.store.dto.StoreListResponseDTO;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.repository.StoreRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

public class StoreService {
	@Value("${store.files.save.path}")
	String savePath;
	
	private final StoreRepository storeRepository;
//	private final SellerService sellerService;
	
	public StoreService(StoreRepository storeRepository) {
		this.storeRepository = storeRepository;
//		this.sellerService = sellerService;
	}
	
	// loginId에 해당하는 모든 가게 조회
	public List<StoreListResponseDTO> getStoreListByLoginId(String loginId) {
		
		return storeRepository.findAllBySeller_LoginId(loginId)
				.stream()
				.map(entity -> entityToStoreListResponseDTO(entity))
				.collect(Collectors.toList());
	}
	
	// 가게 등록
	public void insertStore(StoreDetailsDTO storeDetailsDTO) {
		storeRepository.save(StoreDetailsDTOToEntity(storeDetailsDTO));
		
		// 이미지 추가 구현 필요
	}
	
	// 사업자등록번호 중복 확인
	public boolean checkCorporateRegistrationNumberDuplication(String corporateRegistrationNumber) {
		return storeRepository.existsByCorporateRegistrationNumber(corporateRegistrationNumber);
	}
	
	// 가게 수정
	@Transactional
	public void updateStoreByStoreId(Long storeId, StoreDetailsDTO storeDetailsDTO) {
		Store storeEntity = storeRepository.findById(storeId)
			.orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + storeId));
		
		storeEntity.updateDetails(
								storeDetailsDTO.getName(),
								storeDetailsDTO.getDescription(),
								storeDetailsDTO.getAddress(),
								storeDetailsDTO.getNotice(),
								storeDetailsDTO.getOpenTime(),
								storeDetailsDTO.getCloseTime(),
								storeDetailsDTO.getHolidays());
		
		// 이미지 변경 구현 필요
		
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
