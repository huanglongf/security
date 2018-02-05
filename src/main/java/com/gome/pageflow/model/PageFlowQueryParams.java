package com.gome.pageflow.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * 
 * @author chixiaoyong
 *
 */
public class PageFlowQueryParams implements Serializable {

	private static final long serialVersionUID = -2165079776877105322L;

	private String dataSource;

	private String startTime;
	private String endTime;
	private String pageContent;
	private String showHour;

	private int pageStart;
	private int pageSize;
	private String dim;
	private String search;
	private String orderField = "pv";
	
	private String dataSourceLike;
	
	/**
	 * 过滤小时数据中含有昨天小时的数据
	 */
	private String hourFilter;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPageUrl() {
		return pageContent;
	}

	public void setPageUrl(String pageUrl) {
		this.pageContent = pageUrl;
	}

	public String getShowHour() {
		return showHour;
	}

	public void setShowHour(String showHour) {
		this.showHour = showHour;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public int getPageStart() {
		return pageStart;
	}

	public void setPageStart(int pageStart) {
		this.pageStart = pageStart > 0 ? (pageStart - 1) * pageSize : 0;
	}

	public String getDim() {
		return dim;
	}

	public void setDim(String dim) {
		this.dim = dim;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getPageContent() {
		return pageContent;
	}

	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
	}

	public String getHourFilter() {
		return hourFilter;
	}

	public void setHourFilter(String hourFilter) {
		this.hourFilter = hourFilter;
	}

	public String getOrderField() {
		return orderField;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public String getDataSourceLike() {
		return dataSourceLike;
	}

	public void setDataSourceLike(String dataSourceLike) {
		this.dataSourceLike = dataSourceLike;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataSource == null) ? 0 : dataSource.hashCode());
		result = prime * result + ((dataSourceLike == null) ? 0 : dataSourceLike.hashCode());
		result = prime * result + ((dim == null) ? 0 : dim.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((hourFilter == null) ? 0 : hourFilter.hashCode());
		result = prime * result + ((orderField == null) ? 0 : orderField.hashCode());
		result = prime * result + ((pageContent == null) ? 0 : pageContent.hashCode());
		result = prime * result + pageSize;
		result = prime * result + pageStart;
		result = prime * result + ((search == null) ? 0 : search.hashCode());
		result = prime * result + ((showHour == null) ? 0 : showHour.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageFlowQueryParams other = (PageFlowQueryParams) obj;
		if (dataSource == null) {
			if (other.dataSource != null)
				return false;
		} else if (!dataSource.equals(other.dataSource))
			return false;
		if (dataSourceLike == null) {
			if (other.dataSourceLike != null)
				return false;
		} else if (!dataSourceLike.equals(other.dataSourceLike))
			return false;
		if (dim == null) {
			if (other.dim != null)
				return false;
		} else if (!dim.equals(other.dim))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (hourFilter == null) {
			if (other.hourFilter != null)
				return false;
		} else if (!hourFilter.equals(other.hourFilter))
			return false;
		if (orderField == null) {
			if (other.orderField != null)
				return false;
		} else if (!orderField.equals(other.orderField))
			return false;
		if (pageContent == null) {
			if (other.pageContent != null)
				return false;
		} else if (!pageContent.equals(other.pageContent))
			return false;
		if (pageSize != other.pageSize)
			return false;
		if (pageStart != other.pageStart)
			return false;
		if (search == null) {
			if (other.search != null)
				return false;
		} else if (!search.equals(other.search))
			return false;
		if (showHour == null) {
			if (other.showHour != null)
				return false;
		} else if (!showHour.equals(other.showHour))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PageFlowQueryParams [dataSource=" + dataSource + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", pageContent=" + pageContent + ", showHour=" + showHour + ", pageStart=" + pageStart + ", pageSize="
				+ pageSize + ", dim=" + dim + ", search=" + search + ", orderField=" + orderField + ", dataSourceLike="
				+ dataSourceLike + ", hourFilter=" + hourFilter + "]";
	}


	

}
