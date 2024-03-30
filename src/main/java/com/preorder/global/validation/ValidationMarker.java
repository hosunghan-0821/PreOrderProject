package com.preorder.global.validation;

import javax.validation.groups.Default;

public class ValidationMarker {
    public interface OnCreate extends Default {}

    public interface OnUpdate extends Default {}

    public interface OnRegisterOrder extends Default {}
}
