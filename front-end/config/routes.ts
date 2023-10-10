export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        path: '/user', routes: [
          {name: 'Login', path: '/user/login', component: './user/Login'},
          {name: 'Register', path: '/user/register', component: './user/Register'}
        ]
      },
      {component: './404'},
    ],
  },
  {path: '/welcome', name: 'Welcome', icon: 'smile', component: './Welcome'},
  {
    path: '/admin',
    name: 'Admin pages',
    icon: 'crown',
    access: 'canAdmin',
    component: './Admin',
    routes: [
      {path: '/admin/user-manage', name: 'User management', icon: 'smile', component: './Admin/UserManage'},
      {component: './404'},
    ],
  },
  {name: 'Table search', icon: 'table', path: '/list', component: './TableList'},
  {path: '/', redirect: '/welcome'},
  {component: './404'},
];
