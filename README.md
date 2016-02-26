# spring-data-marklogic

Library for using integrating with Marklogic using spring-data conventions. For example: 

```
import com.baldy.marklogic.repo.MarkLogicRepository;
import com.baldy.marklogic.test.entity.Taxi;

public interface TaxiService extends MarkLogicRepository<Taxi> {

}
```

will instruct Spring to instantiate a MarkLogicRepositoryImpl that handles Taxi class that can be used without writing any more code. A complete sample is included with the unit test.

Special configuration is only the declaration of the marklogic repository bean factory:

```
@Configuration
@EnableJpaRepositories(
    basePackageClasses = TaxiService.class,
    repositoryFactoryBeanClass = MarkLogicRepositoryFactoryBean.class,
    repositoryImplementationPostfix = "CustomImpl"
)
@ComponentScan
@EnableAutoConfiguration
public class TestConfig {

}
```
