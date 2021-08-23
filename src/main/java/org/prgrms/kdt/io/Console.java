package org.prgrms.kdt.io;

import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import org.prgrms.kdt.command.CommandType;
import org.prgrms.kdt.file.FileUtil;
import org.prgrms.kdt.voucher.Voucher;
import org.prgrms.kdt.voucher.VoucherData;

/**
 * Created by yhh1056
 * Date: 2021/08/17 Time: 11:32 오후
 */
public class Console implements Output, Input {

    private final static String GUIDE = "Voucher_Program_Guide";
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void guide() {
        FileUtil.read(GUIDE);
    }

    @Override
    public CommandType inputCommand() {
        try {
            return CommandType.valueOf(scanner.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandType.ERROR;
        }
    }

    @Override
    public VoucherData inputVoucher() {
        printVoucherChoice();

        while (true) {
            try {
                return new VoucherData(scanner.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @Override
    public void printVoucherChoice() {
        System.out.println("생성하고 싶은 바우처를 선택하고 할인양 또는 할인율을 입력해주세요. (쉼표 ',' 를 사용하여 구분해주세요)");
        System.out.println("1. Fixed Amount Voucher");
        System.out.println("2. PercentDiscount Voucher");
        System.out.println("ex) 입력 예시 1, 1000 또는 2, 20");
    }

    @Override
    public void successCreate() {
        System.out.println("성공적으로 등록되었습니다.");
        printNextCommand();
    }

    @Override
    public void printNextCommand() {
        System.out.println("다음 명령을 입력해주세요.");
    }

    @Override
    public void printVouchers(Map<UUID, Voucher> vouchers) {
        vouchers.values().forEach(System.out::println);
    }

    @Override
    public void commandError() {
        System.out.println("지원하지 않는 명령어입니다. 다시 입력해주세요.");
    }
}