package com.pinyougou.sellers.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.group.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellers.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbGoods goods) {

       // TbGoods tbGoods = goods.getGoods();
        goods.setAuditStatus("0");
        goodsMapper.insert(goods);
    }

    private void setItemValues(TbItem item,Goods goods, TbGoods good) {
            item.setGoodsId(good.getId());
            item.setSellerId(good.getSellerId());
            item.setCategoryid(goods.getGoods().getCategory3Id());
            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());

            //品牌名称
            TbBrand brand = brandMapper.selectByPrimaryKey(good.getBrandId());
            item.setBrand(brand.getName());
            // 分类名称
            String itemName = itemCatMapper.selectByPrimaryKey(good.getCategory3Id()).getName();
            item.setCategory(itemName);
            // 商家名称
            String sellerNickName = sellerMapper.selectByPrimaryKey(good.getSellerId()).getNickName();
            item.setSeller(sellerNickName);
            // 图片地址
            List<Map> itemImages= JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
            if (itemImages.size()>0){
                item.setImage((String) itemImages.get(0).get("url"));
            }
            itemMapper.insert(item);
    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        // 修改商品信息
        goods.getGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        // 修改商品扩展信息:
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        // 修改SKU信息: 先删除旧的，重新插入新的

        // 查询旧的sku商品:
        TbItemExample example = new TbItemExample();
        com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        // 删除旧的SKU的信息:
        itemMapper.deleteByExample(example);
        // 保存SKU的信息
        setItemList(goods);
    }

    private void setItemList(Goods goods){
        // 判断是否启用规格
        if("1".equals(goods.getGoods().getIsEnableSpec())){ // 启用规格
            // 保存SKU列表的信息:
            for(TbItem item:goods.getItemList()){
                // 设置SKU的数据：
                String title = goods.getGoods().getGoodsName();
                Map<String,String> map = JSON.parseObject(item.getSpec(), Map.class);
                for (String key : map.keySet()) {
                    title+= " "+map.get(key); // 拼接标题
                }
                item.setTitle(title);
                // sku赋值
                setValue(goods,item);
                // 保存sku商品
                itemMapper.insert(item);
            }
        }else{
            // 没有启用规格 设置默认值
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());
            item.setPrice(goods.getGoods().getPrice());
            item.setNum(999);
            item.setStatus("0");
            item.setIsDefault("1");
            item.setSpec("{}");
            //item.setSpec(new HashMap());
            setValue(goods,item);
            itemMapper.insert(item);
        }

    }

    private void setValue(Goods goods,TbItem item){
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
        if(imageList.size()>0){
            item.setImage((String)imageList.get(0).get("url"));
        }

        // 保存三级分类的ID:
        item.setCategoryid(goods.getGoods().getCategory3Id());
        // 设置日期
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        // 设置商品ID
        item.setGoodsId(goods.getGoods().getId());
        // 设置买家id
        item.setSellerId(goods.getGoods().getSellerId());

        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        // 设置sku分类名称
        item.setCategory(itemCat.getName());

        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        // 设置sku品牌名称
        item.setBrand(brand.getName());

        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        // 设置卖家名称
        item.setSeller(seller.getNickName());
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        goods.setGoods(goodsMapper.selectByPrimaryKey(id));
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        goods.setItemList(tbItems);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(goods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void saveGood(Goods goods) {
        TbGoods good = goods.getGoods();
        good.setAuditStatus("0");
        goodsMapper.insert(good); // 商品表（spu）
        goods.getGoodsDesc().setGoodsId(good.getId());
        goodsDescMapper.insert(goods.getGoodsDesc()); // 商品描述
        if ("1".equals(good.getIsEnableSpec())){
            for (TbItem item : goods.getItemList()) { // sku
                // 标题
                String title = goods.getGoods().getGoodsName();
                // 规格
                Map<String, Object> spec = JSON.parseObject(item.getSpec());
                for (String s : spec.keySet()) {
                    title += " " + spec.get(s); // 拼接title 小米 红米2 白色 电信合约版 电信4G手机 双卡双待 不含合约计划
                }
                item.setTitle(title);
                setItemValues(item,goods, good);
            }
        }else{
            TbItem item=new TbItem();
            item.setTitle(good.getGoodsName());//商品KPU+规格描述串作为SKU名称
            item.setPrice(good.getPrice() );//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量
            item.setSpec("{}");
            setItemValues(item,goods, good);
        }
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
        }
    }

}
