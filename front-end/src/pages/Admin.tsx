import React from 'react';
import {PageHeaderWrapper} from '@ant-design/pro-layout';

// 这个文件定义了Admin的主页面，在page下还有一个Admin的文件夹，里面有Usermanagement是Admin pages下的
//    子页面，如果想要添加更多的子页面，只需要在ADmin的文件夹下再添加文件夹即可，并且再router中添加路径
const Admin: React.FC = (props) => {
  const {children} = props;
  return (
    <PageHeaderWrapper>
      {children}
    </PageHeaderWrapper>
  );
};

export default Admin;
