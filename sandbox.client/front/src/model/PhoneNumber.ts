import {PhoneType} from "./PhoneType";

export class PhoneNumber {
  public number: string;
  public type: PhoneType;

  public constructor(number: string, type: PhoneType) {
    this.number = number;
    this.type = type;
  }
}

