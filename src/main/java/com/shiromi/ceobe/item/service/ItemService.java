package com.shiromi.ceobe.item.service;

import com.shiromi.ceobe.item.dto.ItemDTO;
import com.shiromi.ceobe.item.entity.ItemEntity;
import com.shiromi.ceobe.item.repository.ItemRepository;
import com.shiromi.ceobe.itemFile.entity.ItemFileEntity;
import com.shiromi.ceobe.itemFile.repository.ItemFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemFileRepository itemFileRepository;

    //저장
    public Long save(ItemDTO itemDTO) throws IOException {
        if (itemDTO.getItemFile().get(0).isEmpty()) {
            System.out.println("파일 없음");
            ItemEntity itemEntity = ItemEntity.toItemSaveEntity(itemDTO);
            return itemRepository.save(itemEntity).getId();
        } else {
            System.out.println("파일 있음");
            ItemEntity itemEntity = ItemEntity.toItemSaveFileEntity(itemDTO);
            Long savedId = itemRepository.save(itemEntity).getId();
            ItemEntity entity = itemRepository.findById(savedId).get();
            for (MultipartFile itemFile : itemDTO.getItemFile()) {
                String originalFileNameItem = itemFile.getOriginalFilename();
                String storedFileNameItem = System.currentTimeMillis() + "-" + originalFileNameItem;
                String savePath = "C:\\springboot_img_final\\" + storedFileNameItem;
                itemFile.transferTo(new File(savePath));
                ItemFileEntity itemFileEntity = ItemFileEntity.toSaveItemFileEntity(entity, originalFileNameItem, storedFileNameItem);
                itemFileRepository.save(itemFileEntity);
            }
            return savedId;
        }
    }

    //이름으로 검색
    @Transactional
    public ItemDTO findById(Long id) {
        Optional<ItemEntity> itemEntityOptional = itemRepository.findById(id);
        if (itemEntityOptional.isPresent()) {
            return ItemDTO.toItemDTO(itemEntityOptional.get());
        } else {
            return null;
        }
    }
}
