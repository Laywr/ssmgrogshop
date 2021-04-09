package com.gx.web;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gx.page.Page;
import com.gx.po.AttributePo;
import com.gx.po.CommodityPo;
import com.gx.po.StayRegisterPo;
import com.gx.po.UserPo;
import com.gx.service.StayRegisterService;

@Controller
@RequestMapping("/DateView")
public class DateView {
	
	@Autowired
	private StayRegisterService stayRegisterService;

	@RequestMapping("/tolist")
	public ModelAndView list(){
		ModelAndView mv=null;
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);         //获取年
		int month = cal.get(Calendar.MONTH) + 1;   //获取月
		List<Integer> monthList = new ArrayList<>();
		List<Integer> yearList = new ArrayList<>();
		List<Timestamp> timestampList = new ArrayList<>();
		for (int i = 0; i < 13; i++) {
			if( i == 0){
				monthList.add(month);
			}else {
				monthList.add(month-i);
			}
		}
		List<String> monthStringList = new ArrayList<>();
		for(Integer months : monthList){
			if(months > 0){
				yearList.add(year);
				monthStringList.add(String.valueOf(months));
			}else {
				yearList.add(year-1);
				monthStringList.add(String.valueOf(months+12));
			}
		}
		for (String s : monthStringList) {
			System.out.println(s);
		}
		String[] strings = monthStringList.toArray(new String[monthStringList.size()]);
		for (int i = 0; i < strings.length; i++) {
			if(strings[i].length() == 1){
				strings[i]="0"+strings[i];
			}
		}
		monthStringList = Arrays.asList(strings);
		List<List<StayRegisterPo>> stayRegisterPoList = new ArrayList<>();
		for (int i = 0; i < 13; i++) {
			System.out.println(yearList.get(i)+"-"+monthStringList.get(i)+"-"+"01"+" 00:00:00");
			timestampList.add(Timestamp.valueOf(yearList.get(i)+"-"+monthStringList.get(i)+"-"+"01"+" 00:00:00"));
			if(i != 0){
				stayRegisterPoList.add(stayRegisterService.selectShuJuTongJi(timestampList.get(i),timestampList.get(i-1)));
			}
		}
		mv=new ModelAndView("/dateview/shili");
		Integer count = 1;
		for(List<StayRegisterPo> stayRegisterPos:stayRegisterPoList) {
			double individualSum = 0D;
			double teamSum = 0D;
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
		for (int i = 1; i < 13; i++) {
			mv.addObject("year"+i,yearList.get(i)+"年"+monthStringList.get(i)+"月");
		}
		return mv;
	}

}
