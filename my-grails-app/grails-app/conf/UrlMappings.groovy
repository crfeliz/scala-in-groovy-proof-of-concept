class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/hello-scala"(controller: "helloScala") {
            action = [GET: "index"]
        }

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
