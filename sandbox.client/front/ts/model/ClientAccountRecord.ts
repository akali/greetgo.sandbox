export class ClientAccountInfo {
  public clientId: number/*int*/;
  public clientFullName: string;
  public clientCharmName: string;
  public clientAge: number/*int*/;
  public totalAccBalance: number /*float*/;
  public maxAccBalance: number/*float*/;
  public minAccBalance: number/*float*/;

  public assign(o: any): ClientAccountInfo {
    this.clientFullName = o.fullName;
    this.clientCharmName = o.clientCharmName;
    this.clientAge = o.clientAge;
    this.totalAccBalance = o.totalAccBalance;
    this.maxAccBalance = o.maxAccBalance;
    this.minAccBalance = o.minAccBalance;
    return this;
  }

  public static copy(a: any): ClientAccountInfo {
    let result = new ClientAccountInfo();
    result.assign(a);
    return result;
  }
}
