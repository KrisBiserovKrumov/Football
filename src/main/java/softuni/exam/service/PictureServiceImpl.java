package softuni.exam.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.constants.GlobalConstants;
import softuni.exam.domain.dtos.PictureSeedRoodDto;
import softuni.exam.domain.entities.Picture;
import softuni.exam.repository.PictureRepository;
import softuni.exam.util.ValidatorUtil;
import softuni.exam.util.XmlParser;


import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Service
public class PictureServiceImpl implements PictureService {

    private final PictureRepository pictureRepository;
    private final ModelMapper modelMapper;
    private final ValidatorUtil validatorUtil;
    private final XmlParser xmlParser;

    @Autowired
    public PictureServiceImpl(PictureRepository pictureRepository,
                              ModelMapper modelMapper, ValidatorUtil validatorUtil,
                              XmlParser xmlParser) {
        this.pictureRepository = pictureRepository;
        this.modelMapper = modelMapper;
        this.validatorUtil = validatorUtil;
        this.xmlParser = xmlParser;
    }

    @Override
    public String importPictures() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        PictureSeedRoodDto pictureSeedRoodDto = this.xmlParser
                .ConvertFromFile(GlobalConstants.PICTURES_FILE_PATH,PictureSeedRoodDto.class);
        pictureSeedRoodDto.getPictures().forEach(pictureSeedDto -> {
            if (this.validatorUtil.isValid(pictureSeedDto)){

                if (this.pictureRepository.findByUrl(pictureSeedDto.getUrl()) == null){

                    Picture picture = this.modelMapper.map(pictureSeedDto,Picture.class);

                    this.pictureRepository.saveAndFlush(picture);

                    sb.append("Successfully imported picture - ")
                            .append(picture.getUrl())
                            .append(System.lineSeparator());
                }else {
                    sb.append("Already in DB")
                            .append(System.lineSeparator());
                }
            }else {
                sb.append("Invalid picture")
                        .append(System.lineSeparator());
            }
        });
       return sb.toString();
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count() > 0;
    }

    @Override
    public String readPicturesXmlFile() throws IOException {
        return Files.readString(Path.of(GlobalConstants.PICTURES_FILE_PATH));
    }

    @Override
    public Picture getPictureByUrl(String url) {
        return this.pictureRepository.findByUrl(url);
    }


}
