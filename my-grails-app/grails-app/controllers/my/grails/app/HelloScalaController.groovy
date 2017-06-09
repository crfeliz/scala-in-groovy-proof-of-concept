package my.grails.app

import example.Channel
import example.ChannelImpl
import example.JavaArgs
import groovy.transform.Canonical
import groovy.transform.CompileStatic

class HelloScalaController {

    Channel channel = new ChannelImpl()

    def index() {
        render new String(channel.publish(new GroovyArgs()))
    }
}

@CompileStatic
@Canonical
class SmallerArgs extends JavaArgs {
    public String x

    SmallerArgs(String x) {
        this.x = x
    }
}

@CompileStatic
@Canonical
class GroovyArgs extends JavaArgs {
    public String x = "Scala"
    public String y = "and"
    public String z = "Groovy"
    public JavaArgs moreArgs = new SmallerArgs("are cousins!")
    public Map relatedData = [bla: "blabbidy bloo bla"]
}
