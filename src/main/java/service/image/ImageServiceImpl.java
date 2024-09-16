package service.image;

import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import model.Image.Image;
import model.Image.ImageDto;
import model.product.Product;
import org.hibernate.annotations.processing.SQL;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import repository.image.ImageRepository;
import service.product.ProductServiceImpl;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements IImageService {

    private final ImageRepository imageRepository;
    private final ProductServiceImpl productService;

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Image not found"));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id).ifPresentOrElse(imageRepository::delete, () ->
        {
            throw new ResourceNotFoundException("Image not found");
        });

    }

    @Override
    public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
        Product product = productService.getProductById(productId);

        List<ImageDto> savedImageDtos = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                String buildDownloadUrl = "/api/v1/images/image/download/";
                String downloadUrl = buildDownloadUrl + image.getId();
                image.setDownloadUrl(downloadUrl);

                Image savedImaged = imageRepository.save(image);
                savedImaged.setDownloadUrl(buildDownloadUrl + savedImaged.getId());
                imageRepository.save(savedImaged);

                ImageDto imageDto = new ImageDto();
                imageDto.setImageId(savedImaged.getId());
                imageDto.setImageName(savedImaged.getFileName());
                imageDto.setDownloadUrl(savedImaged.getDownloadUrl());
                savedImageDtos.add(imageDto);

            } catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImageDtos;
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = imageRepository.getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
