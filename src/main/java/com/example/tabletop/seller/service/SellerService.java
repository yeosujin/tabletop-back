package com.example.tabletop.seller.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.tabletop.seller.dto.SellerDTO;
import com.example.tabletop.seller.dto.SellerResponseDTO;
import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.exception.DuplicateLoginIdException;
import com.example.tabletop.seller.exception.InvalidSellerDataException;
import com.example.tabletop.seller.exception.SellerNotFoundException;
import com.example.tabletop.seller.repository.SellerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SellerService {

	private final PasswordEncoder passwordEncoder;
	private final SellerRepository sellerRepository;

	public void signUp(SellerDTO sellerDto) {
		log.info("새로운 판매자를 등록합니다: {}", sellerDto);
		sellerDto.setPassword(passwordEncoder.encode(sellerDto.getPassword()));
		sellerDto.setDoneClickCountSetting(false);

		Seller seller = SellerDTO.toEntity(sellerDto);
		sellerRepository.save(seller);
	}

	public void isLoginIdDuplicate(String loginId) throws DuplicateLoginIdException {
		log.info("로그인 ID 중복 여부를 확인합니다: {}", loginId);
		if (sellerRepository.findByLoginId(loginId).isPresent()) {
			throw new DuplicateLoginIdException("이미 존재하는 ID입니다.");
		}
	}
	
	public SellerResponseDTO.SellerInfoDTO getSeller(String loginId) throws SellerNotFoundException {
		log.info("로그인 ID로 판매자 정보를 조회합니다: {}", loginId);
        Seller seller = sellerRepository.findByLoginId(loginId)
                .orElseThrow(() -> new SellerNotFoundException("해당 판매자를 찾을 수 없습니다."));
        
        return SellerResponseDTO.SellerInfoDTO.toDTO(seller);
    }

	public SellerResponseDTO updateSeller(String loginId, SellerDTO sellerDto) throws SellerNotFoundException, InvalidSellerDataException {
		log.info("로그인 ID {}로 판매자 정보를 업데이트합니다: {}", loginId, sellerDto);
        Seller seller = sellerRepository.findByLoginId(loginId)
                .orElseThrow(() -> new SellerNotFoundException("해당 판매자를 찾을 수 없습니다."));
        
        boolean isUpdated = false;

        if (sellerDto.getEmail() != null && !sellerDto.getEmail().equals(seller.getEmail())) {
            seller.setEmail(sellerDto.getEmail());
            isUpdated = true;
        }

        if (sellerDto.getPassword() != null && !passwordEncoder.matches(sellerDto.getPassword(), seller.getPassword())) {
            seller.setPassword(passwordEncoder.encode(sellerDto.getPassword()));
            isUpdated = true;
        }

        if (sellerDto.getUsername() != null && !sellerDto.getUsername().equals(seller.getUsername())) {
            seller.setUsername(sellerDto.getUsername());
            isUpdated = true;
        }

        if (sellerDto.getMobile() != null && !sellerDto.getMobile().equals(seller.getMobile())) {
            seller.setMobile(sellerDto.getMobile());
            isUpdated = true;
        }

        if (sellerDto.getDoneClickCountSetting() != null && !sellerDto.getDoneClickCountSetting().equals(seller.getDoneClickCountSetting())) {
            seller.setDoneClickCountSetting(sellerDto.getDoneClickCountSetting());
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new InvalidSellerDataException("업데이트할 정보가 없습니다.");
        }

        Seller updatedSeller = sellerRepository.save(seller);
        SellerResponseDTO responseDto = SellerResponseDTO.toDTO(updatedSeller);
        responseDto.setPassword(sellerDto.getPassword());
        return responseDto;
    }
	
	/*
	 삭제 시 n+1문제 발생
	 
Hibernate: 
	select
        s1_0.seller_id,
        s1_0.created_at,
        s1_0.done_click_count_setting,
        s1_0.email,
        s1_0.login_id,
        s1_0.mobile,
        s1_0.password,
        s1_0.refresh_token,
        s1_0.updated_at,
        s1_0.username 
    from
        seller s1_0 
    where
        s1_0.login_id=?
Hibernate: 
    select
        s1_0.seller_id,
        s1_0.created_at,
        s1_0.done_click_count_setting,
        s1_0.email,
        s1_0.login_id,
        s1_0.mobile,
        s1_0.password,
        s1_0.refresh_token,
        s1_0.updated_at,
        s1_0.username 
    from
        seller s1_0 
    where
        s1_0.login_id=?
Hibernate: 
    select
        s1_0.seller_id,
        s1_0.store_id,
        s1_0.address,
        s1_0.close_date,
        s1_0.close_time,
        s1_0.corporate_registration_number,
        s1_0.created_at,
        s1_0.description,
        i1_0.image_id,
        i1_0.file_original_name,
        i1_0.filename,
        i1_0.filepath,
        i1_0.parent_id,
        i1_0.parent_type,
        s1_0.name,
        s1_0.notice,
        s1_0.open_date,
        s1_0.open_time,
        s1_0.store_type,
        s1_0.updated_at 
    from
        store s1_0 
    left join
        image i1_0 
            on i1_0.image_id=s1_0.image_id 
    where
        s1_0.seller_id=?
Hibernate: 
    select
        m1_0.store_id,
        m1_0.id,
        m1_0.created_at,
        m1_0.description,
        m1_0.image_id,
        m1_0.is_available,
        m1_0.name,
        m1_0.price,
        m1_0.updated_at 
    from
        menu m1_0 
    where
        m1_0.store_id=?
Hibernate: 
    select
        o1_0.store_id,
        o1_0.order_id,
        o1_0.created_at,
        p1_0.id,
        p1_0.amount,
        p1_0.created_at,
        p1_0.is_refunded,
        p1_0.payment_method,
        p1_0.transaction_id,
        p1_0.updated_at,
        o1_0.status,
        o1_0.table_number,
        o1_0.total_price,
        o1_0.updated_at,
        o1_0.waiting_number 
    from
        orders o1_0 
    left join
        payment p1_0 
            on o1_0.order_id=p1_0.order_id 
    where
        o1_0.store_id=?
Hibernate: 
    select
        oi1_0.order_id,
        oi1_0.orderitem_id,
        oi1_0.menu_id,
        oi1_0.price,
        oi1_0.quantity 
    from
        orderitem oi1_0 
    where
        oi1_0.order_id=?
Hibernate: 
    update
        store 
    set
        address=?,
        close_date=?,
        close_time=?,
        corporate_registration_number=?,
        description=?,
        image_id=?,
        name=?,
        notice=?,
        open_date=?,
        open_time=?,
        seller_id=?,
        store_type=?,
        updated_at=? 
    where
        store_id=?
Hibernate: 
    update
        orderitem 
    set
        menu_id=?,
        order_id=?,
        price=?,
        quantity=? 
    where
        orderitem_id=?
Hibernate: 
    delete 
    from
        holiday 
    where
        holiday_id=?
Hibernate: 
    delete 
    from
        menu 
    where
        id=?
Hibernate: 
    delete 
    from
        image 
    where
        image_id=?
Hibernate: 
    delete 
    from
        orderitem 
    where
        orderitem_id=?
Hibernate: 
    delete 
    from
        payment 
    where
        id=?
Hibernate: 
    delete 
    from
        orders 
    where
        order_id=?
Hibernate: 
    delete 
    from
        store 
    where
        store_id=?
Hibernate: 
    delete 
    from
        seller 
    where
        seller_id=?
	 */
	public void deleteSeller(String loginId) throws SellerNotFoundException {
		log.info("로그인 ID {}의 판매자를 삭제합니다.", loginId);
		Optional<Seller> seller = sellerRepository.findByLoginId(loginId);
		if (seller.isPresent()) {
			Seller sellerEntity = seller.get();
			sellerRepository.delete(sellerEntity);
		} else {
			log.error("해당 로그인 ID의 판매자를 찾을 수 없어 계정 삭제에 실패합니다: {}", loginId);
			throw new SellerNotFoundException("해당 로그인 ID의 판매자를 찾을 수 없어 계정 삭제에 실패했습니다.");
		}
	}
}