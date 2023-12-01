import os

USERNAME = "jhihyangwu"
PASSWORD = "a4608"

def main():
    print("Make sure you ran 'set autocommit off' on 'sqlpl username@oracle.aloe'")
    print("Make sure you ran 'export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}' on command line")
    print("This script can be ran multiple times, just remember to enter Y if the tables need to be dropped first")
    print()
    all_statements = []
    # If the tables are already created, delete them first.
    already_created = None
    while already_created not in ["Y", "N"]:
        already_created = input("Are the tables already created on oracle.aloe? (Y/N) ")
    if already_created == "Y":
        all_statements += deleteAllTables()
    # Create tables.
    all_statements += createTransactionTable()
    all_statements += createEquipmentTable()
    all_statements += createBorrowedTable()
    all_statements += createMemberTable()
    all_statements += createPackageTable()
    all_statements += createMembershipsTable()
    all_statements += createTrainerTable()
    all_statements += createCourseTable()
    # Fill tables.
    all_statements += fillTransactionTable()
    all_statements += fillEquipmentTable()
    all_statements += fillBorrowedTable()
    all_statements += fillMemberTable()
    all_statements += fillPackageTable()
    all_statements += fillMembershipsTable()
    all_statements += fillTrainerTable()
    all_statements += fillCourseTable()
    # Actually run the Oracle SQL statements.
    with open("temp1528.txt", "w") as file:
        for line in all_statements:
            file.write(line.replace("\n", "") + "\n")
    os.system(f"java InitRelations.java {USERNAME} {PASSWORD}")

def deleteAllTables():
    retval = []
    retval.append("drop table Transaction")
    retval.append("drop table Equipment")
    retval.append("drop table Borrowed")
    retval.append("drop table Member")
    retval.append("drop table Package")
    retval.append("drop table Memberships")
    retval.append("drop table Trainer")
    retval.append("drop table Course")
    retval.append("commit")
    return retval

def createTransactionTable():
    retval = []
    retval.append("""create table Transaction (
        MemberID integer,
        Amount float,
        TransactionDate char(10),
        TransactionType varchar2(20),
        Paid char(1),
        primary key (MemberID)
    )""")
    retval.append("commit")
    return retval

def createEquipmentTable():
    retval = []
    retval.append("""create table Equipment (
        EquipmentID integer,
        EquipmentName varchar2(20),
        Available integer,
        MaxQuantity integer,
        primary key (EquipmentID)
    )""")
    retval.append("commit")
    return retval

def createBorrowedTable():
    retval = []
    retval.append("""create table Borrowed (
        MemberID integer,
        EquipmentID integer,
        QuantityBorrowed integer,
        CheckOutTime char(10),
        ReturnTime char(10),
        isLost char(1),
        primary key (MemberID, EquipmentID, CheckOutTime)
    )""")
    retval.append("commit")
    return retval

def createMemberTable():
    retval = []
    retval.append("""create table Member (
        MemberID integer,
        Name varchar2(50),
        PhoneNum char(12),
        AccountBalance float,
        MembershipID integer,
        AmountSpent float,
        primary key (MemberID)
    )""")
    retval.append("commit")
    return retval

def createPackageTable():
    retval = []
    retval.append("""create table Package (
        PackageID integer,
        Name varchar2(50),
        Price float,
        primary key (PackageID)
    )""")
    retval.append("commit")
    return retval

def createMembershipsTable():
    retval = []
    retval.append("""create table Memberships (
        MembershipID integer,
        Name varchar2(50),
        MinSpend float,
        Discount integer,
        primary key (MembershipID)
    )""")
    retval.append("commit")
    return retval

def createTrainerTable():
    retval = []
    retval.append("""create table Trainer (
        TrainerID integer,
        Name varchar2(50),
        PhoneNum char(12),
        primary key (TrainerID)
    )""")
    retval.append("commit")
    return retval

def createCourseTable():
    retval = []
    retval.append("""create table Course (
        CourseID integer,
        Name varchar2(20),
        TrainerID integer,
        StartTime integer,
        StartDate char(10),
        EndDate char(10),
        Duration integer,
        MaxEnrolled integer,
        CurrentEnrolled integer,
        primary key (CourseID)
    )""")
    retval.append("commit")
    return retval

def fillTransactionTable():
    retval = []
    retval.append("delete from Transaction")  # delete all tuples
    retval.append("insert into Transaction values (0, 300, '2023-11-30', 'Recharge', 'N')")
    retval.append("commit")
    return retval

def fillEquipmentTable():
    retval = []
    retval.append("delete from Equipment")  # delete all tuples
    retval.append("insert into Equipment values (0, 'Basketball', 30, 31)")
    retval.append("commit")
    return retval

def fillBorrowedTable():
    retval = []
    retval.append("delete from Borrowed")  # delete all tuples
    retval.append("insert into Borrowed values (0, 0, 1, '2023-11-29', '', 'N')")
    retval.append("commit")
    return retval

def fillMemberTable():
    retval = []
    retval.append("delete from Member")  # delete all tuples
    retval.append("insert into Member values (0, 'JhihYang', 000-000-0000, 1234.5, 0, 20)")
    retval.append("commit")
    return retval

def fillMemberEnrolledTable():
    retval = []
    retval.append("delete from MemberEnrolled")  # delete all tuples
    retval.append("insert into MemberEnrolled values (0, 0)")
    retval.append("commit")
    return retval

def fillPackageTable():
    retval = []
    retval.append("delete from Package")  # delete all tuples
    retval.append("insert into Package values (0, 'Swimming Bundle', 99.9)")
    retval.append("commit")
    return retval

def fillMembershipsTable():
    retval = []
    retval.append("delete from Memberships")  # delete all tuples
    retval.append("insert into Memberships values (0, 'Diamond', 500, 10)")
    retval.append("insert into Memberships values (1, 'Gold', 1000, 20)")
    retval.append("commit")
    return retval

def fillTrainerTable():
    retval = []
    retval.append("delete from Trainer")  # delete all tuples
    retval.append("insert into Trainer values (0, 'Dr. McCann', 111-111-1111)")
    retval.append("commit")
    return retval

def fillCourseTable():
    retval = []
    retval.append("delete from Course")  # delete all tuples
    retval.append("insert into Course values (0, 'Swim 101', 0, 1400, '2023-08-23', '2023-12-02', 60, 50, 1)")
    retval.append("commit")
    return retval

if __name__ == "__main__":
    main()

