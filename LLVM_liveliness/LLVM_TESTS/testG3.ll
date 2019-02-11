; ModuleID = '/afs/inf.ed.ac.uk/user/s15/s1509922/testG3.c'
source_filename = "/afs/inf.ed.ac.uk/user/s15/s1509922/testG3.c"
target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-unknown-linux-gnu"

; Function Attrs: noinline nounwind uwtable
define dso_local i32 @f(i64*, i64) #0 {
  %3 = alloca i32, align 4
  %4 = alloca i64*, align 8
  %5 = alloca i64, align 8
  %6 = alloca i64, align 8
  store i64* %0, i64** %4, align 8
  store i64 %1, i64* %5, align 8
  store i64 0, i64* %6, align 8
  br label %7

; <label>:7:                                      ; preds = %25, %2
  %8 = load i64, i64* %6, align 8
  %9 = load i64, i64* %5, align 8
  %10 = sdiv i64 %9, 2
  %11 = icmp slt i64 %8, %10
  br i1 %11, label %12, label %28

; <label>:12:                                     ; preds = %7
  %13 = load i64*, i64** %4, align 8
  %14 = load i64, i64* %6, align 8
  %15 = getelementptr inbounds i64, i64* %13, i64 %14
  %16 = load i64, i64* %15, align 8
  %17 = load i64*, i64** %4, align 8
  %18 = load i64, i64* %6, align 8
  %19 = mul nsw i64 2, %18
  %20 = getelementptr inbounds i64, i64* %17, i64 %19
  %21 = load i64, i64* %20, align 8
  %22 = icmp sgt i64 %16, %21
  br i1 %22, label %23, label %24

; <label>:23:                                     ; preds = %12
  store i32 0, i32* %3, align 4
  br label %29

; <label>:24:                                     ; preds = %12
  br label %25

; <label>:25:                                     ; preds = %24
  %26 = load i64, i64* %6, align 8
  %27 = add nsw i64 %26, 1
  store i64 %27, i64* %6, align 8
  br label %7

; <label>:28:                                     ; preds = %7
  store i32 1, i32* %3, align 4
  br label %29

; <label>:29:                                     ; preds = %28, %23
  %30 = load i32, i32* %3, align 4
  ret i32 %30
}

attributes #0 = { noinline nounwind uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 8.0.0 (https://github.com/llvm-mirror/clang c9a4041e1b5e0e635d3c5b522bb7af5d62e93524) (https://github.com/llvm-mirror/llvm 92d120add28960f85eea486cc4d621740baa9b84)"}
