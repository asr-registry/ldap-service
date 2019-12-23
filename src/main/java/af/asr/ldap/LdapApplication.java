package af.asr.ldap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication
public class LdapApplication {

	public static void main(String[] args) {
		SpringApplication.run(LdapApplication.class, args);
	}
	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
//		LOG.info("Jackson2ObjectMapperBuilder bean used!");

		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
//		builder.serializers(new NameSerializer());
		builder.indentOutput(true);
		return builder;
	}
}
