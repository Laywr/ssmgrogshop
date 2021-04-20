package com.gx.web;

import com.gx.po.StayRegisterPo;
import com.gx.service.StayRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/DateView")
public class DateView {
	
	@Autowired
	private StayRegisterService stayRegisterService;

	@RequestMapping("/tolist")
	public ModelAndView list(){
		ModelAndView mv=new ModelAndView("/dateview/shili");
		Calendar cal = Calendar.getInstance();
		//获取年
		int year = cal.get(Calendar.YEAR);
		//获取月
		int month = cal.get(Calendar.MONTH) + 1;
		List<Integer> monthList = new ArrayList<>();
		List<Integer> yearList = new ArrayList<>();
		List<Timestamp> timestampList = new ArrayList<>();
		//添加前一年的月份
		for (int i = 0; i < 13; i++) {
			if( i == 0){
				monthList.add(month);
			}else {
				monthList.add(month-i);
			}
		}
		List<String> monthStringList = new ArrayList<>();
		//添加前一年的年份
		for(Integer months : monthList){
			if(months > 0){
				yearList.add(year);
				monthStringList.add(String.valueOf(months));
			}else {
				yearList.add(year-1);
				monthStringList.add(String.valueOf(months+12));
			}
		}
		//将个位数的月份前加0
		String[] strings = monthStringList.toArray(new String[monthStringList.size()]);
		for (int i = 0; i < strings.length; i++) {
			if(strings[i].length() == 1){
				strings[i]="0"+strings[i];
			}
		}
		monthStringList = Arrays.asList(strings);
		List<List<StayRegisterPo>> stayRegisterPoList = new ArrayList<>();
		for (int i = 0; i < 13; i++) {
			//格式化各月份
			timestampList.add(Timestamp.valueOf(yearList.get(i)+"-"+monthStringList.get(i)+"-"+"01"+" 00:00:00"));
			if(i != 0){
				//查询各月份的销售额
				stayRegisterPoList.add(stayRegisterService.selectShuJuTongJi(timestampList.get(i),timestampList.get(i-1)));
			}
		}
		Integer count = 1;
		for(List<StayRegisterPo> stayRegisterPos:stayRegisterPoList) {
			double individualSum = 0D;
			double teamSum = 0D;
			//分别添加个人和团体的销售额到模型视图中
			for (int i = 0; i < stayRegisterPos.size(); i++) {
				if (stayRegisterPos.get(i).getReceiveTargetID() == 2) {
					individualSum += stayRegisterPos.get(i).getSumConst();
				} else {
					teamSum += stayRegisterPos.get(i).getSumConst();
				}
			}
			mv.addObject("sZongFeiYong" + (count++), individualSum);
			mv.addObject("tZongFeiYong" + count, teamSum);
		}
		//添加日期到模型视图中
		for (int i = 1; i < 13; i++) {
			mv.addObject("year"+i,yearList.get(i)+"年"+monthStringList.get(i)+"月");
		}
		return mv;
	}

}
