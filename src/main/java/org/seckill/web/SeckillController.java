package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


//@Component @Service ��ע����뵽��������
//url:ģ��/��Դ/{id}/ϸ��  /seckill/list
@Controller
/*@RequestMapping("/seckill")*/
public class SeckillController {
	//���ڴ����Ӧ����־
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	//model���������Ⱦlist.jsp������
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String list(Model model){
		//��ȡ�б�ҳ
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list",list);
		//list.jsp + model = ModelAndView
		/*<property name="prefix" value="/WEB-INF/jsp"/>
		<property name="suffix" value=".jsp"/>:/WEB-INF/jsp/list.jsp
		 */		
		return "list";
	}


	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	public String detail(@PathVariable("seckillId")Long seckillId,Model model){

		/*��ʹ��@RequestMapping URI template ��ʽӳ��ʱ�� �� someUrl/{paramId}, ��ʱ��paramId��ͨ�� @Pathvariableע�������������ֵ�������Ĳ����ϡ�
            ʾ�����룺
	@Controller  
	@RequestMapping("/owners/{ownerId}")  
	public class RelativePathUriTemplateController {  

	@RequestMapping("/pets/{petId}")  
	public void findPet(@PathVariable String ownerId, @PathVariable String petId, Model model) {      
 	// implementation omitted   
		}  
	}  
	��������URI template �б��� ownerId��ֵ��petId��ֵ���󶨵������Ĳ����ϡ��������������ƺ���Ҫ�󶨵�uri template�б������Ʋ�һ�£���Ҫ��@PathVariable("name")ָ��uri template�е����ơ�*/

		if(seckillId==null){
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		//����û���㴩��һ��Id����ɱ���󲻴��ڵĻ�
		if(seckill==null){
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}

	//ajax json ��עcontentType:������ĵ��е���������
	@RequestMapping(value="/{seckillId}/exposer",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	
	//����ע�ⷵ��json����
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
		SeckillResult<Exposer> result;
		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true,exposer);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			result = new SeckillResult<Exposer>(false,e.getMessage());
		}
		return result;
	}

	@RequestMapping(value="/{seckillId}/{md5}/execution",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,@CookieValue(value = "killPhone",required = false) Long userPhone,@PathVariable("md5") String md5){
		//userPhone param��cookie param���л�ȡ��
		//����Ϊfalse��ʾ���Ϊ��Ҳ���Բ��Ǳ���ģ������Ƿ��ش���
		//����ʹ��springmvc valid
		if(userPhone == null){
			return new SeckillResult<SeckillExecution>(false, "δע��");
		}
		SeckillResult<SeckillExecution> result;
		try {
			/*SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);*/
			//ͨ���洢����ȥ����
			SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, userPhone, md5);
			return new SeckillResult<SeckillExecution>(true, execution);
		}catch(RepeatKillException e){
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(true,execution);
		}catch(SeckillCloseException e){
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.END);
			return new SeckillResult<SeckillExecution>(true, execution);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(true, execution);
		}
	}
		//���ϵͳ������ʱ��
		@RequestMapping(value="/time/now",method=RequestMethod.GET)
		@ResponseBody
		public SeckillResult<Long> time(){
			Date now = new Date();
			return new SeckillResult(true,now.getTime());
		}
	}