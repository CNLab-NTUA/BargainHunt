package gr.ntua.cn.zannis.bargains.client.dto.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gr.ntua.cn.zannis.bargains.client.dto.meta.Meta;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Manufacturer;

import java.util.List;

/**
 * @author zannis <zannis.kal@gmail.com
 */
public class ManufacturerResponse extends RestResponseImpl<Manufacturer> {

    @JsonCreator
    public ManufacturerResponse(@JsonProperty("manufacturer") Manufacturer manufacturer,
                                @JsonProperty("manufacturers") List<Manufacturer> manufacturers,
                                @JsonProperty("meta") Meta meta) {
        this.item = manufacturer;
        this.items = manufacturers;
        this.meta = meta;
    }

}
