package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.OfferTempModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author starp
 */
public interface OfferTempRepo extends JpaRepository<OfferTempModel,String> {
}
