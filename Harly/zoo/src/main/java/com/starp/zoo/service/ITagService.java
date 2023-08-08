package com.starp.zoo.service;



import com.starp.zoo.common.BadRequestException;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.OfferTagModel;
import com.starp.zoo.entity.zoo.TagModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import com.starp.zoo.vo.TagsOptionVO;

import java.util.List;

/***
 * 
 * @Author David
 * @Date 11:40 2019/3/4
 * @param  
 * @return 
 **/
public interface ITagService {
    /***
     * fetch all tag
     * @Author David
     * @Date 11:48 2019/3/4
     * @param  page
     * @param limit
     * @param type
     * @param name
     * @return com.starp.zoo.entity.common.PageVo
     **/
    PageVO getTagList(int page, int limit, String type, String name);

    /***
     * find model by Id
     * @Author David
     * @Date 12:12 2019/3/4
     * @param  id
     * @return com.starp.zoo.entity.zoo.ResultTagModel
     **/
    TagModel findTagById(String id);

    /***
     * save model
     * @Author David
     * @Date 12:20 2019/3/4
     * @param  tagModel
     * @return void
     **/
    void save(TagModel tagModel);

    /***
     * delete model
     * @Author David
     * @Date 12:26 2019/3/4
     * @param  id
     * @return void
     **/
    void delete(String id);

    /***
     * multi Delete
     * @Author David
     * @Date 13:15 2019/3/4
     * @param  ids
     * @return void
     **/
    void multiDelete(List<String> ids);


    /***
     * find all name
     * @Author David
     * @Date 15:39 2019/3/4
     * @param name
     * @return java.util.List<com.starp.zoo.entity.common.OptionVo>
     **/
    List<OptionVO> findTagName(String name);


    /**
     * find offers
     * @param id
     * @return
     */
    List<OfferTagModel> findOffers(String id);

    /***
     * save offerTag
     * @Author David
     * @Date 11:02 2019/3/6
     * @param  tagModelList
     * @param id
     * @return void
     **/
    void saveTagOffer(List<String> tagModelList,String id);


    /**
     * 获取所有tag选项
     * @return
     */
    TagsOptionVO getAllOptions();

    /**
     * get offer tag
     * @param id
     * @return
     */
    List<OfferTagModel> getOfferTag(String id);


    /**
     * save tag
     * @param model
     * @param offerIds
     * @return
     * @throws BadRequestException
     */
    TagModel saveTag(TagModel model, List<String> offerIds) throws BadRequestException;
}
