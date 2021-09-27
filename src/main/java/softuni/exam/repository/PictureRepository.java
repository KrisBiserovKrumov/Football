package softuni.exam.repository;

import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import softuni.exam.domain.entities.Picture;

@Registered
public interface PictureRepository extends JpaRepository<Picture,Long> {

    Picture findByUrl(String url);

}
