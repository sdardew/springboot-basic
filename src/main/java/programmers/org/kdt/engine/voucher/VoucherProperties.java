package programmers.org.kdt.engine.voucher;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//@Component
@Configuration
@ConfigurationProperties(prefix = "kdt") // gettersetter 필요
public class VoucherProperties implements InitializingBean {

  //@Value("v1.1.1") // 자동 주입된다.
  //@Value("${kdt.version2:v0.0.0}") // 변수가 없다면 이것으로 주입해라.
  private String version;

  //@Value("0")
  private int minimumOrderAmount;

  //@Value("${kdt.support-vendors}")
  private List<String> supportVendors;

  private String description;

  private String blackListFile;

  private final static Logger logger = LoggerFactory.getLogger(VoucherProperties.class);

  @Value("${HOME}")
  private String home;

  @Override
  public void afterPropertiesSet() throws Exception {
    /*System.out.println(MessageFormat.format("[OrderProperties] version -> {0}", version));
    System.out.println(MessageFormat.format("[OrderProperties] minimumOrderAmount -> {0}", minimumOrderAmount));
    System.out.println(MessageFormat.format("[OrderProperties] supportVendors -> {0}", supportVendors));
    System.out.println(MessageFormat.format("[OrderProperties] home -> {0}", home));*/

    logger.debug("[OrderProperties] version -> {}", version);
    logger.debug("[OrderProperties] minimumOrderAmount -> {}", minimumOrderAmount);
    logger.debug("[OrderProperties] supportVendors -> {}", supportVendors);
    logger.debug("[OrderProperties] home -> {}", home);
    
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public int getMinimumOrderAmount() {
    return minimumOrderAmount;
  }

  public void setMinimumOrderAmount(int minimumOrderAmount) {
    this.minimumOrderAmount = minimumOrderAmount;
  }

  public List<String> getSupportVendors() {
    return supportVendors;
  }

  public void setSupportVendors(List<String> supportVendors) {
    this.supportVendors = supportVendors;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setBlackListFile(String blackListFile) { this.blackListFile = blackListFile; }

  public String getBlackListFile() {
    return blackListFile;
  }
}
