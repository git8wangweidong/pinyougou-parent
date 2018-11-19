package com.pinyougou.sellers.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 查询所有品牌
     * @return List<TbBrand>
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult findByPage(TbBrand brand, Integer pageNum, Integer pageSize);

    /**
     * 添加
     * @param brand
     */
    public void add(TbBrand brand);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public TbBrand findById(Long id);

    /**
     * 更新
     * @param brand
     */
    public void update(TbBrand brand);

    /**
     * 批量删除
     * @param ids
     */
    public void batchDelete(Long[] ids);

    public List<Map> selectOptionList();
}