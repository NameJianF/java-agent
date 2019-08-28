import java.security.ProtectionDomain
import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap

import org.sirenia.agent.JavaAgent
import org.codehaus.groovy.control.CompilerConfiguration

/*
-javaagent:d:/git-repo/java-agent/target/java-agent-0.0.1-SNAPSHOT-jar-with-dependencies.jar=/tomcat/groovy
-javaagent:/home/wt/IdeaProjects/java-agent/target/java-agent-0.0.1-SNAPSHOT-jar-with-dependencies.jar
*/
class ClassFileTransformer{
    def classNameMatcher
    def config = new CompilerConfiguration()
    def classLoaderProxyMap = new ConcurrentHashMap()

    def init(){
        info("init ClassFileTransformer")
        File file = new File(JavaAgent.groovyFileDir, "ClassNameMatcher.groovy")
        config.setSourceEncoding("UTF-8")
        def cl = Thread.currentThread().getContextClassLoader()
        def groovyClassLoader = new GroovyClassLoader(cl, config)
        classNameMatcher = groovyClassLoader.parseClass(file).newInstance()
        classNameMatcher.init()
    }

    def info(String msg){
        def time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())
        println "[$time] $msg"
    }
    def transform(ClassLoader classLoader, String className, Class<?> clazz, ProtectionDomain domain, byte[] bytes){
        try{
            return doTransform(classLoader, className)
        }catch(e){
            System.err.println("transform error: ${className}")
            e.printStackTrace()
            throw e
        }
    }

    private doTransform(ClassLoader classLoader, String className) {
        if(classNameMatcher==null){
            //println "classNameMatcher is null"
            return null
        }
        if (classLoader.getClass().getName().contains('GroovyClassLoader')) {
            return null
        }

        //类名使用/分隔的，替换成.分隔
        className = className.replace("/", ".")

        if (!classNameMatcher.match(className)) {
            return null
        }

        def classProxy = classLoaderProxyMap[classLoader]
        if (!classProxy) {
            File file = new File(JavaAgent.groovyFileDir, "ClassProxy.groovy")
            def clName = classLoader.toString()
            if(clName.length()>80){
                clName = clName.substring(0,80)+'...'
            }
            info "parse ClassProxy for $clName"
            def groovyClassLoader = new GroovyClassLoader(classLoader, config)
            classProxy = groovyClassLoader.parseClass(file).newInstance()
            classLoaderProxyMap[classLoader] = classProxy
            classProxy.init(classLoader)
        }
        classProxy.proxy(className)
    }

}