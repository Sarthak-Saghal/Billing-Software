package in.bushansirgur.billingsoftware.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import in.bushansirgur.billingsoftware.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );

            // Cloudinary image URL
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error uploading file to Cloudinary"
            );
        }
    }

    @Override
    public boolean deleteFile(String imgUrl) {
        try {
            // extract public_id from URL
            String publicId = extractPublicId(imgUrl);

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );

            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error deleting file from Cloudinary"
            );
        }
    }

    private String extractPublicId(String url) {
        // example:
        // https://res.cloudinary.com/demo/image/upload/v12345/sample.jpg
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
}
