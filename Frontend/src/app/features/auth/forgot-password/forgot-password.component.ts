import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {

  step = 1; // 1 = Email, 2 = OTP + Reset
  email = '';
  otp = '';
  newPassword = '';
  confirmNewPassword = '';

  showPassword = false;
  showConfirmPassword = false;

  errorMsg = '';
  successMsg = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) { }

  onSendOtp() {
    if (!this.email) return;

    this.loading = true;
    this.errorMsg = '';
    this.successMsg = '';

    this.auth.forgotPassword(this.email).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMsg = 'OTP sent to your email.';
        this.step = 2;
      },
      error: (err) => {
        this.loading = false;
        this.errorMsg = err.error?.error || 'Failed to send OTP. Email might not exist.';
      }
    });

  }

  onResetPassword() {
    if (this.newPassword !== this.confirmNewPassword) {
      this.errorMsg = 'Passwords do not match!';
      return;
    }

    this.loading = true;
    this.errorMsg = '';
    this.successMsg = '';

    const data = {
      email: this.email,
      otp: this.otp,
      newPassword: this.newPassword
    };

    this.auth.resetPassword(data).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMsg = 'Password reset successfully! Redirecting to login...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMsg = err.error?.error || 'Failed to reset password. Invalid OTP?';
      }
    });
  }

  allowOnlyNumbers(event: any) {
    const input = event.target;
    let value = input.value;
    value = value.replace(/[^0-9]/g, '');
    if (value.length > 6) {
      value = value.slice(0, 6);
    }
    this.otp = value;
    input.value = value;
  }
}
