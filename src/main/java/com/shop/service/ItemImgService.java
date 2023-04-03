package com.shop.service;


import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletContext;
import java.io.File;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private  final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ServletContext servletContext;
    private final FileService fileService;



    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if(!StringUtils.isEmpty(oriImgName)) {
            String path = servletContext.getRealPath("/");
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes(), path);
            imgUrl = "/images/item/" + imgName; // 수정된 경로
        }

        // 상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        if(!itemImgFile.isEmpty()) {
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            // 기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())) {
                String path = servletContext.getRealPath("/");
                String filePath = path + itemImgLocation + "/" + savedItemImg.getImgName();
                File file = new File(filePath);
                if(file.exists() && file.isFile()) {
                    file.delete();
                }
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = "";
            String imgUrl = "";
            if(!StringUtils.isEmpty(oriImgName)) {
                String path = servletContext.getRealPath("/");
                imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes(), path);
                imgUrl = "/images/item/" + imgName;
            }

            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }

    public void deleteItemImg(Long itemImgId) throws EntityNotFoundException {
        ItemImg itemImg = itemImgRepository.findById(itemImgId)
                .orElseThrow(EntityNotFoundException::new);
        Item item = itemRepository.findById(itemImg.getId())
                .orElseThrow(EntityNotFoundException::new);

        item.removeItemImg(itemImg);
        itemImgRepository.delete(itemImg);
    }

}
