; ModuleID = '/afs/inf.ed.ac.uk/user/s15/s1509922/testG2.c'
source_filename = "/afs/inf.ed.ac.uk/user/s15/s1509922/testG2.c"
target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-unknown-linux-gnu"

; Function Attrs: noinline nounwind uwtable
define dso_local i32 @isord(i64*, i64, i32 (i8*, i8*)*) #0 {
  %4 = alloca i32, align 4
  %5 = alloca i64*, align 8
  %6 = alloca i64, align 8
  %7 = alloca i32 (i8*, i8*)*, align 8
  %8 = alloca i64, align 8
  store i64* %0, i64** %5, align 8
  store i64 %1, i64* %6, align 8
  store i32 (i8*, i8*)* %2, i32 (i8*, i8*)** %7, align 8
  store i64 1, i64* %8, align 8
  br label %9

; <label>:9:                                      ; preds = %28, %3
  %10 = load i64, i64* %8, align 8
  %11 = load i64, i64* %6, align 8
  %12 = icmp slt i64 %10, %11
  br i1 %12, label %13, label %31

; <label>:13:                                     ; preds = %9
  %14 = load i32 (i8*, i8*)*, i32 (i8*, i8*)** %7, align 8
  %15 = load i64*, i64** %5, align 8
  %16 = load i64, i64* %8, align 8
  %17 = getelementptr inbounds i64, i64* %15, i64 %16
  %18 = getelementptr inbounds i64, i64* %17, i64 -1
  %19 = bitcast i64* %18 to i8*
  %20 = load i64*, i64** %5, align 8
  %21 = load i64, i64* %8, align 8
  %22 = getelementptr inbounds i64, i64* %20, i64 %21
  %23 = bitcast i64* %22 to i8*
  %24 = call i32 %14(i8* %19, i8* %23)
  %25 = icmp sgt i32 %24, 0
  br i1 %25, label %26, label %27

; <label>:26:                                     ; preds = %13
  store i32 0, i32* %4, align 4
  br label %32

; <label>:27:                                     ; preds = %13
  br label %28

; <label>:28:                                     ; preds = %27
  %29 = load i64, i64* %8, align 8
  %30 = add nsw i64 %29, 1
  store i64 %30, i64* %8, align 8
  br label %9

; <label>:31:                                     ; preds = %9
  store i32 1, i32* %4, align 4
  br label %32

; <label>:32:                                     ; preds = %31, %26
  %33 = load i32, i32* %4, align 4
  ret i32 %33
}

attributes #0 = { noinline nounwind uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 8.0.0 (https://github.com/llvm-mirror/clang c9a4041e1b5e0e635d3c5b522bb7af5d62e93524) (https://github.com/llvm-mirror/llvm 92d120add28960f85eea486cc4d621740baa9b84)"}
