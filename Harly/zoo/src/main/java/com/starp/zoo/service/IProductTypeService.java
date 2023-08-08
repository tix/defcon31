package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.ProductTypeModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.List;

/**
 * @author Charles
 * Created by eric.luo on 2017/11/8.
 */
public interface IProductTypeService {

    /**
     * 获取ProductTypeModel
     * @Author David
     * @Date 17:15 2018/12/18
     * @param id
     * @return ProductTypeModel
     **/
    ProductTypeModel getProductTypeModel(String id);

    /**
     * 获取符合条件的ProductTypeModel
     * @Author David
     * @Date 17:15 2018/12/18
     * @param
     * @return ProductTypeModel
     **/
    List<ProductTypeModel> getProductTypeList();


    /**
     * 删除ProductTypeModel
     * @Author David
     * @Date 17:15 2018/12/18
     * @param id
     * @return
     **/
    void deleteProductModel(String id);


    /**
     * 保存ProductTypeModel
     * @Author David
     * @Date 17:16 2018/12/18
     * @param productTypeModel
     * @return
     **/
    void saveModel(ProductTypeModel productTypeModel);

    /**
     * 查找所有的名字
     * @return
     */
    List<OptionVO> getAllNames();

    /**
     * 删除
     * @param id
     */
    void deleteById(String id);

    /**
     * 批量删除
     * @param ids
     */
    void multiDelete(List<String> ids);

    /**
     * 翻页查询
     * @param page
     * @param limit
     * @param name
     * @return
     */
    PageVO getList(int page, int limit, String name);
}
