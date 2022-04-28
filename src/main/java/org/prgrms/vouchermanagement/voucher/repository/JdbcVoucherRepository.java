package org.prgrms.vouchermanagement.voucher.repository;

import org.prgrms.vouchermanagement.voucher.voucher.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class JdbcVoucherRepository implements VoucherRepository {

  private static final Logger log = LoggerFactory.getLogger(JdbcVoucherRepository.class);

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public JdbcVoucherRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static RowMapper<Voucher> voucherRowMapper =  (resultSet, i) -> {
    UUID voucherId = toUUID(resultSet.getBytes("voucher_id"));
    long reduction = resultSet.getLong("reduction");
    LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
    VoucherType voucherType = VoucherType.fromDbValue(resultSet.getInt("voucher_type"));
    return VoucherFactory.createVoucher(voucherId, reduction, createdAt, voucherType);
  };


  @Override
  public Voucher insert(Voucher voucher) {
    var update = jdbcTemplate.update(
      "INSERT INTO vouchers(voucher_id, reduction, created_at, voucher_type) VALUES (UUID_TO_BIN(:voucherId), :reduction, :createdAt, :voucherType)",
      toParamMap(voucher));
    if(update != 1) {
      throw new RuntimeException("Nothing was inserted");
    }
    return voucher;
  }

  @Override
  public List<Voucher> findAll() {
    return jdbcTemplate.query("SELECT * FROM vouchers", voucherRowMapper);
  }

  @Override
  public List<Voucher> findByVoucherType(VoucherType voucherType) {
    Map<String, Object> paramMap = new HashMap<>() {{
      put("voucherType", voucherType.toDbValue());
    }};
    return jdbcTemplate.query("SELECT * FROM vouchers WHERE voucher_type = :voucherType ",
      Collections.singletonMap("voucherType", voucherType.toDbValue()),
      voucherRowMapper);
  }

  @Override
  public Optional<Voucher> findById(UUID voucherId) {
    try {
      return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM vouchers WHERE voucher_id = UUID_TO_BIN(:voucherId)",
        Collections.singletonMap("voucherId", voucherId.toString().getBytes()),
        voucherRowMapper));
    } catch (EmptyResultDataAccessException e) {
      log.error("Get empty result", e);
      return Optional.empty();
    }
  }

  @Override
  public int count() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vouchers", Collections.emptyMap(), Integer.class);
  }

  @Override
  public void deleteAll() {
    jdbcTemplate.update("DELETE FROM vouchers", Collections.emptyMap());
  }

  @Override
  public void deleteById(UUID voucherId) {
    try {
      jdbcTemplate.update("DELETE FROM vouchers WHERE voucher_id = UUID_TO_BIN(:voucherId)",
        Collections.singletonMap("voucherId", voucherId.toString().getBytes()));
    } catch (Exception e) {
      log.error("No such voucherId", e);
    }
  }

  @Override
  public void updateById(UUID voucherId, long reduction) {
    Optional<Voucher> voucher = findById(voucherId);
    if(voucher.isEmpty()) {
      log.error("No such voucher");
    }
    Voucher oldVoucher = voucher.get();

    if((VoucherType.fromInstance(oldVoucher) == VoucherType.FIXED_AMOUNT && FixedAmountVoucher.checkReduction(reduction)) ||
      (VoucherType.fromInstance(oldVoucher) == VoucherType.PERCENT_DISCOUNT && PercentDiscountVoucher.checkReduction(reduction))) {
      jdbcTemplate.update("UPDATE vouchers SET reduction = :reduction WHERE voucher_id = UUID_TO_BIN(:voucherId)",
        new Hashtable<>(){{
          put("voucherId", voucherId.toString().getBytes());
          put("reduction", reduction);
        }});
    }
  }

  private Map<String, Object> toParamMap(Voucher voucher) {
    return new HashMap<String, Object>(){{
      put("voucherId", voucher.getVoucherID().toString().getBytes());
      put("reduction", voucher.getReduction());
      put("createdAt", Timestamp.valueOf(voucher.getCreatedAt()));
      put("voucherType", VoucherType.toDbValue(voucher));
    }};
  }

  static UUID toUUID(byte[] bytes) throws SQLException {
    var byteBuffer = ByteBuffer.wrap(bytes);
    return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
  }
}