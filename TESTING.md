# Kết quả kiểm tra sau khi bổ sung đăng ký — 28/10/2026

## Build và unit test đã chạy

- `:app:build :app:assembleDebugAndroidTest`: thành công sau các thay đổi.
- Unit test: **4 passed, 0 skipped, 0 failed**.
- ContactRepositoryTest: vẫn đạt; kiểm tra dữ liệu mẫu, thêm/xóa, UUID, yêu thích, thống kê, số trùng và bản sao danh sách.
- PasswordHasherTest (3 test): khớp vector PBKDF2-HMAC-SHA256 đã biết, nhận đúng/từ chối sai mật khẩu hoặc salt, salt ngẫu nhiên và nhánh PBKDF2-HMAC-SHA1.
- Manifest: LoginActivity vẫn là launcher; RegisterActivity không exported và có adjustResize.
- Không thay đổi Gradle/dependency, 4 màn hình cũ hoặc mã quản lý danh bạ.
- Không có lỗi lint chặn build.

## Kiểm thử thiết bị

Lệnh `:app:connectedDebugAndroidTest` đã được thử và dừng trước khi thực thi test với:

```
DeviceException: No connected devices!
```

Do đó chưa xác nhận thực tế giao diện đăng ký/đăng nhập mới trên Emulator. Kết quả build và unit test không thay thế kiểm thử thiết bị.

Các test thiết bị đã biên dịch:

- AccountRepositoryTest: tạo tài khoản bằng tên duy nhất, kiểm tra trùng/admin, mở lại repository và đăng nhập, từ chối sai mật khẩu, kiểm tra bản ghi có hash/salt và không chứa mật khẩu thuần. Chỉ xóa tài khoản do test tạo.
- RegisterWorkflowTest: mở đăng ký từ Login, trường trống, mật khẩu ngắn, xác nhận không khớp, trùng admin, tạo tài khoản và đăng nhập vào danh bạ.
- ContactWorkflowTest: giữ kịch bản hồi quy danh bạ; bổ sung chờ xác thực nền để tránh kiểm tra UI trước khi hoàn tất băm.
- AuthTestWait: chờ tác vụ xác thực kết thúc trong kiểm thử.

## Kiểm tra trên Emulator khi có thiết bị

1. Device Manager → khởi động máy ảo, chờ đến màn hình chính Android rồi chọn app → Run.
2. Đăng nhập admin / 123456, xác nhận 3 liên hệ mẫu và thao tác danh bạ cũ.
3. Mở lại ứng dụng → Đăng ký ngay; thử bỏ trống, mật khẩu dưới 6 ký tự, xác nhận khác, tên admin và tên đã đăng ký.
4. Tạo tài khoản hợp lệ, kiểm tra quay về Login, tên được điền sẵn và có thông báo thành công.
5. Đăng nhập tài khoản mới; thử mật khẩu sai rồi đúng.
6. Dừng tiến trình/mở lại: tài khoản vẫn đăng nhập được; danh bạ trở về 3 mẫu vì vẫn chỉ lưu RAM.
7. Kiểm tra bàn phím, cuộn form, nút quay lại và Back.
8. Chạy `.\gradlew.bat :app:connectedDebugAndroidTest` để chạy bộ test thiết bị.
9. Kiểm tra gọi điện thủ công từ chi tiết: mở trình quay số với đúng số, không thực hiện cuộc gọi.

APK debug: app/build/outputs/apk/debug/app-debug.apk.
