package script.impl

import com.alibaba.fastjson.JSONObject

/*def 'login'(name,pwd,session){
	logger.info "###################################login"
	session.setAttribute('whosyourdaddy','开启无敌模式')
	def json = new JSONObject()
	json.put("qwer","QWER")
	json
}
def 'login-invoke'(self, thisMethod, proceed, args){
	logger.info "###################################login#invoke"
	def session = args[2]
	session.setAttribute('whosyourdaddy','开启无敌模式')
	//proceed.invoke(self,args)
	org.sirenia.agent.AssistInvoker.proceed(self,proceed,args)
}*/
def hello(){
	logger.info 'hello'+"*"*10
}
return this

