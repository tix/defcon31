package com.starp.zoo.service.impl;

import com.starp.zoo.entity.zoo.ProductTypeModel;
import com.starp.zoo.repo.zoo.ProductTypeRepo;
import com.starp.zoo.service.IProductTypeService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Charles
 * Created by eric.luo on 2017/11/8.
 */
@Service
public class ProductTypeServiceImpl implements IProductTypeService {

    @Autowired
    private ProductTypeRepo productTypeRepo;

    @Override
    public ProductTypeModel getProductTypeModel(String id) {
        return productTypeRepo.findFirstByIdentification(id);
    }

    @Override
    public List<ProductTypeModel> getProductTypeList() {
        return productTypeRepo.findAll();
    }

    @Override
    public void deleteProductModel(String id) {
        productTypeRepo.deleteById(id);
    }

    @Override
    public void saveModel(ProductTypeModel productTypeModel) {
        productTypeRepo.save(productTypeModel);
    }

    @Override
    public List<OptionVO> getAllNames() {
        List<ProductTypeModel> productTypeModels = productTypeRepo.findQuery();
        List<OptionVO> optionVOS = new ArrayList<>();
        if (productTypeModels != null && productTypeModels.size() > 0) {
            for(ProductTypeModel productTypeModel : productTypeModels){
                OptionVO optionVO = new OptionVO();
                optionVO.setIdentification(productTypeModel.getIdentification());
                optionVO.setValue(productTypeModel.getProductKey());
                optionVO.setLabel(productTypeModel.getProductValue());
                optionVOS.add(optionVO);
            }
        }
        return optionVOS;
    }

    @Override
    public void deleteById(String id) {
        productTypeRepo.deleteById(id);
    }

    @Override
    public void multiDelete(List<String> ids) {
        if(ids != null && ids.size() > 0) {
            for (String id : ids) {
                deleteById(id);
            }
        }
    }

    @Override
    public PageVO getList(int page, int limit, String name) {
        Specification<ProductTypeModel> specification = new Specification<ProductTypeModel>() {
            @Override
            public Predicate toPredicate(Root<ProductTypeModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                if(!StringUtils.isEmpty(name)) {
                    criteriaQuery.where(criteriaBuilder.equal(root.get("productValue"), name));
                }
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
                return criteriaQuery.getRestriction();
            }
        };
        PageVO<ProductTypeModel> pageVO = new PageVO<>();
        Long total = productTypeRepo.count(specification);
        pageVO.setTotal(total);
        pageVO.setPage(page);
        pageVO.setLimit(limit);
        page = page > 0 ? page -1 : 0;
        pageVO.setList(productTypeRepo.findAll(specification, PageRequest.of(page, limit)).getContent());
        return pageVO;
    }
}
